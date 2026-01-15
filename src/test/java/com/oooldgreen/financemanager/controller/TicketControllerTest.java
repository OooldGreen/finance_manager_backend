package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.repository.BalanceModeRepository;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import com.oooldgreen.financemanager.service.FinanceService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TicketControllerTest {

}
