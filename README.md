# tinyStats

Statistics about data (streaming processing):

* Approximate counting using 4, 8, 16 bits of state.
* Cardinality estimation using HyperLogLog, HyperBitBit, LinearCounting, and hybrid. Some implementations only need 64 bits of state.
* Frequent item detection using count-min sketch, AMS sketch. Some implementations only need 64 bits of state.
* Minimum and maximum.
* Approximate median using the remedian algorithm (somewhat improved).
* Approximate histogram using 64 bits of state with 11 buckets, e.g. for lengths.
* Random sample using the reservoir sampling algorithm.
* Cardinality estimation for key-values pairs, by combining count-min sketch and HyperLogLog.

## Similar Libraries

* https://github.com/mayconbordin/streaminer
