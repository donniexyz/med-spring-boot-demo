/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.donniexyz.demo.med.lib;

import lombok.experimental.UtilityClass;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CashAccountConstants {

    public static final class BD {
        public static final BigDecimal MINUS_ONE = BigDecimal.ONE.subtract(BigDecimal.TWO);

    }

    public static final class MA {
        public static final CurrencyUnit USD = Monetary.getCurrency("USD");

        public static final List<CurrencyUnit> ALLOWED_CURRENCIES = List.of(USD);
        public static final List<String> ALLOWED_CURRENCY_NAMES = ALLOWED_CURRENCIES.stream().map(CurrencyUnit::getCurrencyCode).toList();

        public static final Map<String, CurrencyUnit> CURRENCY_UNIT_MAP = ALLOWED_CURRENCIES.stream().collect(Collectors.toMap(CurrencyUnit::getCurrencyCode, k -> k));

        public static final MonetaryAmount[] ZERO_THROUGH_TEN_USD = {
                Money.of(0, USD),
                Money.of(1, USD),
                Money.of(2, USD),
                Money.of(3, USD),
                Money.of(4, USD),
                Money.of(5, USD),
                Money.of(6, USD),
                Money.of(7, USD),
                Money.of(8, USD),
                Money.of(9, USD),
                Money.of(10, USD),
        };

        public static final MonetaryAmount[] TEN_POWER_USD = {
                Money.of(1, USD),
                Money.of(10, USD),
                Money.of(100, USD),
                Money.of(1_000, USD),
                Money.of(10_000, USD),
                Money.of(100_000, USD),
                Money.of(1_000_000, USD),
                Money.of(10_000_000, USD),
                Money.of(100_000_000, USD),
                Money.of(1_000_000_000, USD),
                Money.of(10_000_000_000L, USD),
                Money.of(100_000_000_000L, USD),
        };

        public static final Map<CurrencyUnit, MonetaryAmount[]> ZERO_THROUGH_TEN_PER_CCY = Map.of(USD, ZERO_THROUGH_TEN_USD);
        public static final Map<CurrencyUnit, MonetaryAmount[]> TEN_POWER_PER_CCY = Map.of(USD, TEN_POWER_USD);

        public static final MonetaryAmount ZERO_USD = ZERO_THROUGH_TEN_USD[0];
        public static final MonetaryAmount ONE_USD = ZERO_THROUGH_TEN_USD[1];
        public static final MonetaryAmount TWO_USD = ZERO_THROUGH_TEN_USD[2];
        public static final MonetaryAmount THREE_USD = ZERO_THROUGH_TEN_USD[3];
        public static final MonetaryAmount FOUR_USD = ZERO_THROUGH_TEN_USD[4];
        public static final MonetaryAmount FIVE_USD = ZERO_THROUGH_TEN_USD[5];
        public static final MonetaryAmount SIX_USD = ZERO_THROUGH_TEN_USD[6];
        public static final MonetaryAmount SEVEN_USD = ZERO_THROUGH_TEN_USD[7];
        public static final MonetaryAmount EIGHT_USD = ZERO_THROUGH_TEN_USD[8];
        public static final MonetaryAmount NINE_USD = ZERO_THROUGH_TEN_USD[9];
        public static final MonetaryAmount TEN_USD = ZERO_THROUGH_TEN_USD[10];

    }
}
