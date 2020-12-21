# tinyStats

Statistics about data (streaming processing):

* Approximate counting using 4, 8, 16 bits of state.
* Cardinality estimation using HyperLogLog, HyperBitBit, LinearCounting, and hybrid. Some implementations only need 64 bits of state.
* Frequent item detection using count-min sketch, count-min-mean sketch, AMS sketch, majority, frequent. Some implementations only need 64 bits of state.
* Cardinality estimation for key-values pairs, by combining count-min-mean sketch and HyperLogLog.
* Approximate histogram using 64 bits of state with 11 buckets, e.g. for lengths.
* Minimum and maximum.
* Approximate median using the remedian algorithm (somewhat improved).
* Random sample using the reservoir sampling algorithm.
* Set reconciliation using IBLT (invertible Bloom lookup table), including a file repair tool similar to PAR2.

## Similar Libraries

* https://github.com/mayconbordin/streaminer
* https://github.com/twitter/algebird
* https://datasketches.apache.org/
