/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.savings.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.organisation.teller.domain.CashierRepository;
import org.apache.fineract.organisation.teller.exception.CashierNotSetException;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.teller.domain.CashierRepository;
import org.apache.fineract.organisation.teller.exception.CashierNotSetException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@CommandType(entity = "SAVINGSACCOUNT", action = "WITHDRAWAL")
public class WithdrawSavingsAccountCommandHandler implements NewCommandSourceHandler {

    private final SavingsAccountWritePlatformService writePlatformService;
    private final PlatformSecurityContext context;
    private final CashierRepository cashierRepository;

    @Autowired
    public WithdrawSavingsAccountCommandHandler(final SavingsAccountWritePlatformService writePlatformService, final PlatformSecurityContext context,final CashierRepository cashierRepository) {
        
        //Yves FOPA - 14 Oct 2025
        this.cashierRepository = cashierRepository;
        this.context = context;
        //end of added code - Yves FOPA

        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        //Yves FOPA - 14 Oct 2025
        //check if the current logged user is configured as cashier
        final AppUser currentUser = this.context.authenticatedUser();
        if (this.cashierRepository.findByStaff_id(currentUser.getStaffId()).isEmpty()){
           throw new CashierNotSetException();
        }
        //log to see if we hit this code
        log.debug("WithdrawORIZON");
        log.debug("WithdrawORIZON {}", command);  
        //end of added code - Yves FOPA

        return this.writePlatformService.withdrawal(command.getSavingsId(), command);
    }
}
