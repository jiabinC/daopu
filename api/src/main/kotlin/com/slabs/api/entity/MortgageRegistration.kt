package com.slabs.api.entity

data class MortgageRegistration (
        var order_id :String?=null,
        var registered_orig_rec_date:String?=null,
        var invoice_orig_rec_date:String?=null,
        var busi_insurance_orig_rec_date:String?=null,
        var cta_insurance_orig_rec_date:String?=null,
        var mortgage_send_person_date:String?=null,
        var mortgage_return_date:String?=null,
        var warrant_send_back_date:String?=null,
        var real_mortgage_date:String?=null,
        var mortgage_date:String?=null,
        var mortgage_operator:String?=null,
        var remarks:String?=null


)