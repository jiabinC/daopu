//package com.abs.flow
//
//import co.paralleluniverse.fibers.Suspendable
//import com.google.common.net.MediaType
//import com.slabs.dapp.contract.CustomerContract
//import com.slabs.dapp.dto.CustomerDTO
//import com.slabs.dapp.state.CustomerState
//import com.slabs.dapp.utils.GroupingPolicy
//import com.slabs.dapp.utils.SealChainRequestBody
//import com.slabs.dapp.utils.SealChainReturnValue
//import com.slabs.nbflow.*
//import com.slabs.utils.HashCalculation
//import net.corda.core.flows.InitiatingFlow
//import net.corda.core.flows.StartableByRPC
//import net.corda.core.identity.Party
//import net.corda.core.transactions.SignedTransaction
//import java.text.SimpleDateFormat
//import java.util.*
//
//object CustomerFlow {
//
//    @InitiatingFlow
//    @StartableByRPC
//    class Create(val data: Array<CustomerDTO>) : FlowLogicX<SignedTransaction>() {
//
//        @Suspendable
//        override fun call(): SignedTransaction {
//
//            // 0.检查上链数据的合法性
//            if (data.isEmpty()) {
//                logger.error("data is empty")
//                throw IllegalArgumentException("data is empty")
//            }
//            if (data.size > 1000) {//需要小于1000条数据
//                logger.error("data is great than 1000")
//                //测试环境，暂时先注释掉。
//                //throw IllegalArgumentException("data is great than 1000")
//            }
//
//            val sealUrl = "http://39.104.136.10:6523/api/txs/payments/seal/4cBmXyjwZAEttRt9u3tBYVyCmWNXdbFjFryZq1spHJ16e7gmKRED3bHjUu@2147483648/SEALxmHMKuYFq9bi686AeXVGf2B2KHPmLMGpKMgvFFCj9zvhpmbCB5vFJES2JpoHMzNiN5ZPsHNJn9WjRs6myMUThBvkhi8fe7asA8d/10000000"
//            //概况：上链返回sealId后，直接构成合法的states,然后这些stats都拥有自己独特的sealId，并存入数据库，然后分发。
//            var contentString = ""
//            var stateList = mutableListOf<CustomerState>()
//            data.map {
//                //1.remarks 内容有 tableName keyfield  hash
//                val className = "com.abs.dto.CustomerDTO"
//                val keyfield = it.CUSTOMER_CODE
//                val tableName = "CUSTOMER"
//                val remakers = HashCalculation.SealUtil.getHash2Seal(className,it,keyfield,tableName)
//                println("remakers: " + remakers)
//                //2.构建请求体
//                var groupingPolicy = GroupingPolicy()
//                var sealChainRequestBody = SealChainRequestBody(groupingPolicy,remakers)
//                contentString  = Gson().toJson(sealChainRequestBody)
//                println("请求的内容：" + contentString)
//                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), contentString)
//                //3.接收并解析返回的sealId
//                var request = Request.Builder()
//                        .url(sealUrl)
//                        .post(requestBody)
//                        .build()
//                print("开始与sealchain对接")
//                print("收到数据:" + OkHttpClient().newCall(request).execute().body()?.string() + "\n")
//                print("结束与sealchain对接")
//                var sealChainReturnValue: String? = OkHttpClient().newCall(request).execute().body()?.string()
//                val json = Gson().fromJson<SealChainReturnValue>(sealChainReturnValue, SealChainReturnValue::class.java)
//                val sealId = json.Right.ctId  //上链返回ctId,这里采用sealId。
//                //4.构建合法的states
//                val state = CustomerState(
//                        customerDTO = it,
//                        createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
//                        fromParty = myParty,
//                        keyfield = it.customer_code,//此数据的唯一标识。直接客户编码
//                        hash = HashCalculation.getHash(it, className),
//                        sealId = sealId//如果上链返回数据为空，直接抛出异常。
//                )
//                stateList.add(state)
//            }
//
//            //2.建立交易并遍历data准备好state
//            val txBuilder = transactionBuilder(defaultNotary)
//            stateList.map {
//                txBuilder.withItems(it + CustomerContract.CONTRACT_ID)
//            }
//            //3.说明此次交易为 create
//            txBuilder.withItems(Commands.createCommand(listOf(myParty)))
//            //4.获取到网络上的其他节点
//            val otherParties = mutableListOf<Party>()
//            serviceHub.networkMapCache.allNodes.forEach {
//                if (it.legalIdentities[0] != defaultNotary) {
//                    otherParties.add(it.legalIdentities[0])
//                }
//            }
//            //5.本节点验证并签名
//            val signedTran = txBuilder.verify().sign()
//            //6.notary进行签名，并记录到本节点数据库
//            val notarySignedTran = notariseAndRecord(signedTran)
//            //7.分发到其他节点
//            subFlow(ReportToRegulatorFlow(otherParties, notarySignedTran))
//
//            logger.info("notarySignedTran is $notarySignedTran")
//            return notarySignedTran
//        }
//    }
//}