package com.slabs.api.entity

/**
 *
 *   经销商基础信息
 */
data class DealerBasicInfo (
        var dealer_name :String ?= null,
        var dealer_place :String ?= null,
        var dealer_type :String ?= null,
        var dealer_level :String ?= null,
        var date_allow :String ?= null,
        var date_sign :String ?= null,
        var date_expire :String ?= null,
        var address :String ?= null,
        var contact_person :String ?= null,
        var tel_no :String ?= null,
        var remarks:String?=null

)