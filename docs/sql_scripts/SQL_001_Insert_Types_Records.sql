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
