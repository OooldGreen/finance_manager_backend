package com.oooldgreen.financemanager.mapper;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.Transaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTransaction(Transaction newT, @MappingTarget Transaction transaction);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    TransactionDTO toDTO(Transaction transaction);
}