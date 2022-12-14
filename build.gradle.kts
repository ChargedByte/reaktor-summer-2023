tasks.withType<Wrapper> {
    gradleVersion = "7.6"
    distributionType = Wrapper.DistributionType.BIN
}

allprojects {
    group = "dev.chargedbyte"
    version = "0.1.0"
}
