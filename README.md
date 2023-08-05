# SIMPLE MODULE TO STORE MATCH LIVE STATS

### ASSUMPTIONS:
1. Live score data has much more reads than writes. Let's say the ratio is 1000:1. The code should be optimized to read.
2. Only one thread is doing update at the same time. 
3. Match live score is not data that have to be consistent. When goal happens at 12:00:00 and user will query at 12:00:01 then he can get inconsistent data. However, data have to be synchronized as soon as possible.
4. There can be some issues with chronological updates. It means that info about update can come before info about new match. Moreover, older update request can be called after newer update request. The correct one is one with the highest score.
5. 

