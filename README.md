# akka-persistence-aggregator
Akka persistence plugin delegating writes to multiple sub-plugins and reads to one of them.

This enables writing journal and snapshots to N DBs (where N is usually == 2) and reading from one, chosen DB. May be useful for projects using akka-persistence and migrating databases in several steps, for example:
- write to DB1, read from DB1
- write to both DB1 and DB2, read from DB1
- write to both DB1 and DB2, read from DB2
- write to DB2, read from DB2
