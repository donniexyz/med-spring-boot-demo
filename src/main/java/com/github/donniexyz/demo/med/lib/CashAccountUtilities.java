package com.github.donniexyz.demo.med.lib;

import com.github.donniexyz.demo.med.enums.BalanceSheetComponentEnum;
import lombok.experimental.UtilityClass;

import javax.money.MonetaryAmount;

@UtilityClass
public class CashAccountUtilities {

    public static MonetaryAmount debit(MonetaryAmount balance, MonetaryAmount amount, BalanceSheetComponentEnum balanceSheetEntry) {
        // should we check the amount must be positive here?
        return balance.add(
                BalanceSheetComponentEnum.LIABILITIES.equals(balanceSheetEntry) || BalanceSheetComponentEnum.EQUITY.equals(balanceSheetEntry)
                        ? amount.negate() : amount);
    }

    public static MonetaryAmount credit(MonetaryAmount balance, MonetaryAmount amount, BalanceSheetComponentEnum balanceSheetEntry) {
        // should we check the amount must be positive here?
        return balance.add(
                BalanceSheetComponentEnum.ASSETS.equals(balanceSheetEntry)
                        ? amount.negate() : amount);
    }

}
