package com.bcebhagalpur.cheashu.dashboard.aws

import com.amazonaws.regions.Regions

object AWSKeys {
    internal const val COGNITO_POOL_ID = "ap-south-1:91f74515-5273-4259-af53-7d293a42389f"
    internal val MY_REGION = Regions.AP_SOUTH_1 // WHAT EVER REGION IT MAY BE, PLEASE CHOOSE EXACT
    const val BUCKET_NAME = "cheashu-room-photos"
    const val TABLE_NAME = "cheashu-room-photos"
}