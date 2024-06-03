-- account_owner
INSERT INTO account_owner (id, email, first_name, last_name, notes, phone_number, type_code) VALUES(0, 'corsec@acme.com', 'ACME BANK', '', 'The Bank itself', '+00100010001', 'BANK_SELF');
INSERT INTO account_owner (id, email, first_name, last_name, notes, phone_number, type_code) VALUES(2, 'jl@skybridge.com', 'Mr Joe', 'Luck S', 'SkyBridge normal employee', '+00100010002', 'INDI');
INSERT INTO account_owner (id, email, first_name, last_name, notes, phone_number, type_code) VALUES(3, 'je@corpA.com', 'Ms Jean', 'Everdeen', 'A normal customer', '+00100010003', 'INDI');
INSERT INTO account_owner (id, email, first_name, last_name, notes, phone_number, type_code) VALUES(4, 'cs@skybridge.com', 'SKYBRIDGE Corp', '', 'A normal corporate customer', '+00100010004', 'CORP');
INSERT INTO account_owner (id, email, first_name, last_name, notes, phone_number, type_code) VALUES(5, 'ms@acme.com', 'Mr Michael', 'Saymore', 'A bank''s normal staff', '+00100010005', 'STAFF');

-- cash_account
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(11, 0.00, NULL, 'Bank Internal Cash Account Asset - under Finance Dept', 'Bank Internal Cash Account Asset', 0, 'INTERNAL_DR');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(12, 0.00, NULL, 'Bank Internal Cash Account Liabilities - under Finance Dept', 'Bank Internal Cash Account Liabilities', 0, 'INTERNAL_CR');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(13, 0.00, NULL, 'Bank Internal Clearing Account Nostro - under Finance Dept', 'Bank Internal Clearing Account Nostro', 0, 'CLEARING');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(14, 0.00, NULL, 'BankAccountof Drawer #1 in Branch X1', 'Bank Branch X1 Drawer #1', 0, 'DRAWERS');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(15, 0.00, NULL, 'Saving Account #1 - owned by Mr Joe Luck S', 'Saving Account #1', 2, 'SAVING');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(16, 0.00, NULL, 'Saving Account #2 - owned by Ms Jean Everdeen', 'Saving Account #2', 3, 'SAVING');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(17, 0.00, NULL, 'Saving Account #3 - owned by SKYBRIDGE Corp', 'Saving Account #3', 4, 'SAVING');
INSERT INTO cash_account (id, balance, last_transaction_date, notes, title, owner_id, type_code) VALUES(18, 0.00, NULL, 'Saving Account #4 - owned by SKYBRIDGE Corp for Online Purchase', 'Saving Account #4', 4, 'SAVING');
