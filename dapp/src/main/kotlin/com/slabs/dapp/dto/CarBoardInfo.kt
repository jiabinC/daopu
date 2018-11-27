package com.slabs.api.entity

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class CarBoardInfo (
        var order_id:String?=null,
        var registration_area:String?=null,
        var vehicle_admin_office:String?=null,
        var registration_no:String?=null,
        var first_registration_date:String?=null,
        var car_boarding_date:String?=null,
        var car_boarding_remarks:String?=null,
        var license_plate:String?=null
)

