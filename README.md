# Cash Account - Springboot demo project
--------------------------------------------

This microservice use springboot 3.3 with hibernate 6.5.x.

## NOTE: This git repository might not be updated/maintained anymore.
I lost my 2FA and passkey of this github account.
Although push via cmd still works now it is unpredictable how it will last,
and it is pathetic and disheartening way of doing.
Most probably I will recreate git repository in gitlab.

------
## Data model
### Business configuration entities:
* AccountOwnerType
* AccountType
    * AccountOwnerTypeApplicableToAccountType
* AccountTransactionType
    * AccountTypeApplicableToTransactionType
* AccountHistoryType

### Business Master entities:
* AccountOwner
* CashAccount

### Business transaction entities:
* AccountTransaction
* AccountTransactionItem
* AccountHistory

------
## Coding approaches:
* Using hibernate L2 cache
* NOT using DTO
* Using lombok and mapstruct to have optimum object duplication / copy properties
* Separation of http facing @Controller and @Service logic 

