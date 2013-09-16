# 缓存交易调用

##	现状
	

目前CRM交易调用存在这样的问题,某个(些)交易调用时间较长,查询的内容重复,或频繁查询.比如产品过滤,用户查询,详单查询. 我们可以通过缓存交易的返回结果,重复调用的时候直接从缓存中获取结果,一定时间内不再重复调用.



## 缓存调用前后的比较


### 缓存前

	╭─rikugun@R-GUN-MINI  ~
	╰─$ ab -c 10 -n200  http://130.59.1.110:7113/BSS/test
	This is ApacheBench, Version 2.3 <$Revision: 655654 $>
	Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
	Licensed to The Apache Software Foundation, http://www.apache.org/

	Benchmarking 130.59.1.110 (be patient)
	Completed 100 requests
	Completed 200 requests
	Finished 200 requests


	Server Software:
	Server Hostname:        130.59.1.110
	Server Port:            7113

	Document Path:          /BSS/test
	Document Length:        0 bytes

	Concurrency Level:      10
	Time taken for tests:   102.874 seconds
	Complete requests:      200
	Failed requests:        0
	Write errors:           0
	Total transferred:      18800 bytes
	HTML transferred:       0 bytes
	Requests per second:    1.94 [#/sec] (mean)
	Time per request:       5143.689 [ms] (mean)
	Time per request:       514.369 [ms] (mean, across all concurrent requests)
	Transfer rate:          0.18 [Kbytes/sec] received

	Connection Times (ms)
	              min  mean[+/-sd] median   max
	Connect:        4   16  49.6      8     674
	Processing:   575 5048 4581.1   3535   19166
	Waiting:      575 5047 4581.1   3535   19165
	Total:        584 5065 4584.7   3551   19174

	Percentage of the requests served within a certain time (ms)
	  50%   3551
	  66%   3624
	  75%   3753
	  80%   4024
	  90%  18431
	  95%  18704
	  98%  18824
	  99%  18964
	 100%  19174 (longest request)
	 
	 
### 缓存后

	 
	╭─rikugun@R-GUN-MINI  ~
	╰─$ ab -c 10 -n200  http://130.59.1.110:7113/BSS/test\?cache\=1
	This is ApacheBench, Version 2.3 <$Revision: 655654 $>
	Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
	Licensed to The Apache Software Foundation, http://www.apache.org/

	Benchmarking 130.59.1.110 (be patient)
	Completed 100 requests
	Completed 200 requests
	Finished 200 requests


	Server Software:
	Server Hostname:        130.59.1.110
	Server Port:            7113

	Document Path:          /BSS/test?cache=1
	Document Length:        0 bytes

	Concurrency Level:      10
	Time taken for tests:   3.653 seconds
	Complete requests:      200
	Failed requests:        0
	Write errors:           0
	Total transferred:      18800 bytes
	HTML transferred:       0 bytes
	Requests per second:    54.75 [#/sec] (mean)
	Time per request:       182.665 [ms] (mean)
	Time per request:       18.266 [ms] (mean, across all concurrent requests)
	Transfer rate:          5.03 [Kbytes/sec] received

	Connection Times (ms)
	              min  mean[+/-sd] median   max
	Connect:        4   13   9.8     10      56
	Processing:    19  115 454.7     25    3412
	Waiting:       19  113 454.7     24    3411
	Total:         23  127 454.4     38    3423

	Percentage of the requests served within a certain time (ms)
	  50%     38
	  66%     44
	  75%     50
	  80%     58
	  90%     73
	  95%     85
	  98%   2386
	  99%   3011
	 100%   3423 (longest request) 


 缓存前后性能相差了34倍
