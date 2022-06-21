# tinyStats

Statistics about data (streaming processing):

* [Approximate counting](https://en.wikipedia.org/wiki/Approximate_counting_algorithm) using 4, 8, 16 bits of state.
* [Cardinality estimation](https://en.wikipedia.org/wiki/Count-distinct_problem) using [HyperLogLog](https://en.wikipedia.org/wiki/HyperLogLog), [HyperBitBit](https://github.com/seiflotfy/hyperbitbit), [linear probabilistic counting](https://www.moderndescartes.com/essays/hyperloglog/), and hybrid. Some implementations only need 64 bits of state.
* [Frequent item detection](http://archive.dimacs.rutgers.edu/Workshops/WGUnifyingTheory/Slides/cormode.pdf) using [count-min sketch](https://en.wikipedia.org/wiki/Count%E2%80%93min_sketch), count-min-mean sketch, AMS sketch, majority, frequent. Some implementations only need 64 bits of state.
* Cardinality estimation for key-values pairs, by combining [count-min-mean sketch](https://observablehq.com/@niyuzheno1/probability-data-structure) and HyperLogLog.
* Approximate histogram using 64 bits of state with 11 buckets, e.g. for lengths.
* Minimum and maximum.
* Approximate median using the [remedian algorithm](https://web.ipac.caltech.edu/staff/fmasci/home/astro_refs/Remedian.pdf) (somewhat improved).
* Random sample using the [reservoir sampling](https://en.wikipedia.org/wiki/Reservoir_sampling) algorithm.
* Set reconciliation using [IBLT](https://arxiv.org/abs/1101.2245) (invertible Bloom lookup table), including a file repair tool similar to [PAR2](https://en.wikipedia.org/wiki/Parchive).

## Similar Libraries

* https://github.com/mayconbordin/streaminer
* https://github.com/twitter/algebird
* https://datasketches.apache.org/
* https://github.com/mattlorimor/ProbabilisticDataStructures
