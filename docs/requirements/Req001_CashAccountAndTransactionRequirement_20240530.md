# Req 001 - CashAccount And Transaction Requirement

Summary:
* Our client is a bank, and the system will be used by bank to keep track customer's saving account
* The system also able to record transactions that happen to the accounts, and transactions might change balance of the account 

------

## Entities
Summary:
* CashAccount is an entity to keep track accounts in bank. It is owned by a party (customer/bank,person/organization), it has balance amount.
* Cash account's balance can be altered by transactions.

### CashAccount

#### User Actions
* Create with zero balance
* Update applicable only to `.title` field
* Delete can happen only to _ with zero balance


### AccountOwner
This is the party that might owns account(s).


### AccountTransaction
This is the transaction that might affect balance of an account.

#### User Actions
* Create will change related account's balance 


### AccountHistory
Every change in account balance will be recorded here.


