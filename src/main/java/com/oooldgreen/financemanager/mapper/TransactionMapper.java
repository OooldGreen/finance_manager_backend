package com.oooldgreen.financemanager.mapper;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.service.TagService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TagService.class})
public interface TransactionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateTransaction(Transaction newT, @MappingTarget Transaction transaction);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "tags", source = "tagNames")
    Transaction toEntity(TransactionDTO dto);
}