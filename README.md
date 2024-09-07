# Cash Account - Springboot demo project
--------------------------------------------

This microservice use springboot 3.3 with hibernate 6.5.x.

NOTE: I am able to login, so we are back!

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

