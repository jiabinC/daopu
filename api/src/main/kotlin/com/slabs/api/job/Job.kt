package com.slabs.api.job

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class Job {

    @Async
    fun test() {

    }

}