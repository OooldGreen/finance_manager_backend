package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.mapper.TransactionMapper;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.repository.TransactionRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final TagService tagService;

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto) {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUser(user);

        Account account = accountRepository.findById(dto.getAccountId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        BigDecimal amount = dto.getAmount();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        transaction.setAccount(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public Page<TransactionDTO> getAllTransactions(Integer year, Integer month, Pageable pageable) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month !=null) ? month : now.getMonthValue();
        User user = userService.getCurrentAuthUser();
        Page<Transaction> transactions = transactionRepository.getMonthlyTransactions(targetYear, targetMonth, user,  pageable);

        return transactions.map(transactionMapper::toDTO);
    }

    @Transactional
    public Map<String, BigDecimal> getMonthTotalBalance(Integer year, Integer month) {
        User user = userService.getCurrentAuthUser();
        List<Transaction> transactions = transactionRepository.getMonthlyTransactions(year, month, user);
        BigDecimal expense = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal income = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("expense", expense);
        result.put("income", income);
        result.put("total", expense.add(income));
        return result;
    }

    @Transactional
    public TransactionDTO getTransaction(Long transactionId) {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        User user = userService.getCurrentAuthUser();
        List<Transaction> transactions = transactionRepository.getTransactionsByAccount(user, accountId);
        return transactions.stream().map(transactionMapper::toDTO).toList();
    }

    @Transactional
    public TransactionDTO updateTransaction(Long transactionId, TransactionDTO newT) {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId()).orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionMapper.updateTransaction(transactionMapper.toEntity(newT), transaction);

        if (newT.getTagNames()!= null) {
            Set <Tag> newTags = newT.getTagNames().stream()
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            transaction.setTags(newTags);
        }

        Transaction updatedT = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedT);
    }

    @Transactional
    public void deleteTransaction(Long transactionId) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId()).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied! You can not delete this record.");
        }

        Account account = accountRepository.findById(transaction.getAccount().getId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        transactionRepository.deleteById(transactionId);
    }

    @Transactional
    public void deleteTransactions(List<Long> ids) {
        Long userId = userService.getCurrentAuthUser().getId();
        transactionRepository.deleteTransactions(ids, userId);
    }

    @Transactional
    public Page<TransactionDTO> searchTransactions(TransactionSearchRequest request) {
        User user = userService.getCurrentAuthUser();
        Specification<Transaction> spec = ((root, query, criteriaBuilder) -> {
            // root: 代表查询的实体（Transaction），可以用 root.get("fieldName") 获取属性
            // query: 定义查询结构（如 DISTINCT）
            // cb (CriteriaBuilder): 构建具体的判断条件（如 equal, like, between）
            List<Predicate> predicates = new ArrayList<>();

            // 1. keyword 模糊搜索
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String pattern = "%" + request.getKeyword() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), pattern),
                        criteriaBuilder.like(root.get("description"), pattern)
                ));
            }

            // 2. date
            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("ticketCompletionDate"), request.getStartDateTime(), request.getEndDateTime()));
            }

            // 3. income / expense
            if (request.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), request.getEnumType()));
            }

            // 4. status
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                predicates.add(root.get("transactionStatus").in(request.getEnumStatus()));
            }

            // 5. categories
            if (request.getCategories() != null && !request.getCategories().isEmpty()) {
                predicates.add(root.get("transactionCategory").in(request.getEnumCategories()));
            }

            // 6. accounts
            List<Long> accountIds = request.getAccountIds();
            if (accountIds != null && !accountIds.isEmpty()) {
                predicates.add(root.get("account").in(accountIds));
            }

            // 7. tags
            List<Long> tagIds = request.getTagIds();
            if (tagIds != null && !tagIds.isEmpty()) {
                predicates.add(root.get("tags").in(tagIds));
            }

            // security check
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortKey()).descending()
                : Sort.by(request.getSortKey()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Transaction> res = transactionRepository.findAll(spec, pageable);
        return res.map(transactionMapper::toDTO);
    }
}
