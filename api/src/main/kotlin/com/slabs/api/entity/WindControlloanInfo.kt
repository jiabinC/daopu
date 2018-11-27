package com.slabs.api.entity


/**
 *
 *  风控贷中信息
 */
data class WindControlloanInfo (
    var order_id:String?=null,                        //   订单编号
    var contract_rec_date:String?=null,            // 贷款合同收到时间
    var contract_rec_remarks:String?=null,//合同收到备注
    var contract_full_date:String?=null,           //贷款合同齐全时间
    var contract_full_remarks:String?=null,       //合同齐全备注
    var contract_send_print_date:String?=null,    //合同移交机打时间
    var contract_send_print_remarks:String?=null, //移交机打备注
    var contract_print_date:String?=null,           //贷款合同机打时间
    var contract_print_operator:String?=null,       //贷款合同机打人员
    var cont_send_bank_date:String?=null,           //合同送交银行时间
    var cont_send_bank_ampm:String?=null,           //上午下午
    var cont_send_bank_remarks:String?=null          //送交银行备注

)