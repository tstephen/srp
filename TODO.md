# TODO

## Issues

- new weighting factors
- migrate users to keycloak
- announce user migration
- migrate registration to keycloak and flowable
- migrate ui to k8s

error with memory:
```
Setting Active Processor Count to 2
Calculating JVM memory based on 113080K available memory
`For more information on this calculation, see https://paketo.io/docs/reference/java-reference/#memory-calculator
unable to calculate memory configuration
fixed memory regions require 646525K which is greater than 113080K available for allocation: -XX:MaxDirectMemorySize=10M, -XX:MaxMetaspaceSize=134525K, -XX:ReservedCodeCacheSize=240M, -Xss1M * 250 threads
ERROR: failed to launch: exec.d: failed to execute exec.d file at path '/layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/memory-calculator': exit status 1
```
