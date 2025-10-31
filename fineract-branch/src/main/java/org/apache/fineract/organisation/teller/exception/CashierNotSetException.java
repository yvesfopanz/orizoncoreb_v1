//Yves FOPA - 15 Oct 2025

package org.apache.fineract.organisation.teller.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class CashierNotSetException extends AbstractPlatformDomainRuleException {

    private static final String ERROR_MESSAGE_CODE = "error.msg.cashier.not.found";
    private static final String DEFAULT_ERROR_MESSAGE = "User is not configured as Cashier !";

    public CashierNotSetException() {
        super(ERROR_MESSAGE_CODE,DEFAULT_ERROR_MESSAGE);
    }

}