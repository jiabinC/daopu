package com.slabs.api.entity

data class BankLoanInfo (
    var order_id:String?=null,
    var loan_bank:String?=null,
    var bank_contract_no:String?=null,
    var client_name:String?=null,
    var client_idcard_no:String?=null,
    var credit_card_rec_date:String?=null,
    var bank_cont_rec_date:String?=null,
    var credit_card_no:String?=null,
    var credit_card_offline_reason:String?=null,
    var bank_loan_date:String?=null,
    var bank_loan_amount:Double?=null,
    var mortgage_service_fee:Double?=null



)