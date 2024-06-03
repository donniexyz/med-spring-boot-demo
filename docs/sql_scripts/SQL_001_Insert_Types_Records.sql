INSERT INTO account_owner_type (individual_or_group, "self", "name", notes, type_code) VALUES('GROUP', true, 'The Finance Institution', 'This is the bank itself', 'BANK_SELF');
INSERT INTO account_owner_type (individual_or_group, "self", "name", notes, type_code) VALUES('INDIVIDUAL', NULL, 'Staffs of the bank', 'Staffs might entitled to special rates, transaction fees discount, etc', 'STAFF');
INSERT INTO account_owner_type (individual_or_group, "self", "name", notes, type_code) VALUES('INDIVIDUAL', NULL, 'Individual customer', 'Myriads of individual customer', 'INDI');
INSERT INTO account_owner_type (individual_or_group, "self", "name", notes, type_code) VALUES('GROUP', NULL, 'Corporate customer', 'Corporate or organization customer', 'CORP');

INSERT INTO account_type (type_code, balance_sheet_entry, minimum_balance, "name", notes) VALUES('SAVING', 'LIABILITIES', 0.00, 'Saving Account', '');
INSERT INTO account_type (type_code, balance_sheet_entry, minimum_balance, "name", notes) VALUES('DRAWERS', 'ASSETS', 0.00, 'Physical Drawer', 'Physical Drawer at Bank Branch');
INSERT INTO account_type (type_code, balance_sheet_entry, minimum_balance, "name", notes) VALUES('CLEARING', 'ASSETS', NULL, 'Clearing Accounts', 'to be used for incoming/outgoing transfer');
INSERT INTO account_type (type_code, balance_sheet_entry, minimum_balance, "name", notes) VALUES('INTERNAL_DR', 'LIABILITIES', NULL, 'Internal Debit Accounts', 'Liability account for internal use only');
INSERT INTO account_type (type_code, balance_sheet_entry, minimum_balance, "name", notes) VALUES('INTERNAL_CR', 'ASSETS', NULL, 'Internal Credit Accounts', 'Assets account for internal use only');

INSERT INTO account_transaction_type (type_code, "name", notes) VALUES('INTERNAL', 'Bank internal transfer', 'to be used for internal bank customer to customer transaction');
INSERT INTO account_transaction_type (type_code, "name", notes) VALUES('INCOMING', 'Incoming transfer', 'to be used for incoming transfer from clearing to customer account');
INSERT INTO account_transaction_type (type_code, "name", notes) VALUES('ADJUSTMENT_DR', 'Adjustment debit transaction', 'Only for internal use only. Requires C Level and/or Finance Dept. approval.');
INSERT INTO account_transaction_type (type_code, "name", notes) VALUES('ADJUSTMENT_CR', 'Adjustment credit transaction', 'Only for internal use only. Requires C Level and/or Finance Dept. approval.');
INSERT INTO account_transaction_type (type_code, "name", notes) VALUES('OUTGOING', 'Outgoing transfer', 'to be used for outgoing transfer from customer account to clearing');

INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('ADJUSTMENT_CR', 'INTERNAL_CR');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('ADJUSTMENT_DR', 'INTERNAL_DR');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('INCOMING', 'CLEARING');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('INCREASE_CLEARING', 'INTERNAL_CR');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('INTERNAL', 'SAVING');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('OUTGOING', 'SAVING');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('REDUCE_CLEARING', 'CLEARING');
INSERT INTO account_transaction_type_applicable_from_account_types (applicable_from_transaction_types_type_code, applicable_from_account_types_type_code) VALUES('TEST', 'SAVING');

INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('ADJUSTMENT_CR', 'INTERNAL_DR');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('ADJUSTMENT_CR', 'INTERNAL_CR');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('ADJUSTMENT_DR', 'INTERNAL_DR');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('ADJUSTMENT_DR', 'INTERNAL_CR');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('INCOMING', 'SAVING');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('INCREASE_CLEARING', 'CLEARING');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('INTERNAL', 'SAVING');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('OUTGOING', 'CLEARING');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('REDUCE_CLEARING', 'INTERNAL_DR');
INSERT INTO account_transaction_type_applicable_to_account_types (applicable_to_transaction_types_type_code, applicable_to_account_types_type_code) VALUES('TEST', 'SAVING');

INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('SAVING', 'STAFF');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('SAVING', 'INDI');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('SAVING', 'CORP');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('DRAWERS', 'BANK_SELF');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('CLEARING', 'BANK_SELF');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('INTERNAL_DR', 'BANK_SELF');
INSERT INTO account_type_applicable_for_account_owner_types (account_type_type_code, applicable_for_account_owner_types_type_code) VALUES('INTERNAL_CR', 'BANK_SELF');
