# SIMPLE MODULE TO STORE MATCH LIVE STATS

### ASSUMPTIONS:
1. Live score data has much more reads than writes. Let's say the ratio is 1000:1. The code should be optimized to read, write can be slow.
2. Many threads can read/write data simultaneously. 
3. Live score board can contain old data, but have to be consistent - matches should not disappear. It cannot happen that match will disappear in any time before finish is called.
4. Match live score is not data that have to be consistent in the score context. When goal happens at 12:00:00 and user will query at 12:00:01 then he can get inconsistent data. However, data have to be synchronized as soon as possible.
5. There can be some issues with chronological updates. It means that info about update can come before info about new match. Moreover, older update request can be called after newer update request. The correct one is one with the highest score.
6. If update come before create then matchStartTime is set to epochMillis of the system.


### IMPLEMENTATION DECISIONS:
1. The data is split into matchMap and liveScoreBoard. LiveScoreBoard is ordered data for the get method's fast access.
2. To avoid data inconsistency in the case when match update and match create came at the same time from two threads simple write lock on all modify methods is implemented.
3. The matchMap is concurrent Collection not because of concurrent access to the class - it solved by write lock, but to avoid jvm memory consistency errors.
