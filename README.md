## Requirements
Docker

## Installation
After cloning the repository, go to the root directory folder and run the following command:
`docker compose up`

## How to Use
`GET /api/debts` Returns a list of debts, you can filter by creditorName, statusId and dueDate  
Status Id  
1 - Created  
2 - Partially Paid  
3 - Paid  

`GET /api/debts/{id}` Returns a debt with the specified id  

`POST /api/debts` Registers a new debt: you need to inform creditorName, totalValue, numberOfInstallments and dueDate  

`POST /api/debts/{debtId}/installments` Pays a installment: you need to inform the correct value of the installment.  
If the debt is overdue you will need to pay the value with an interest rate of 5%
