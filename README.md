# Samza-Esper engine #

Samza-Esper is engine for distributed complex event processing (CEP) which allows to built architecture for real-time analysis leveraging CEP and stream processing (SP) concepts.This engine is simple integration of Apache Samza and Esper engine. This engine allows user to run Esper jobs which consists of multiple Esper instance that can process data in parallel. Jobs can be concated together to create distributed, scalable, dynamic and loosly coupled graph of computation. 

# Deployment #

Esper job is basically Samza job, which tasks contains Esper instances, with additional configuration for Esper. Job is deployed as package on Yarn cluster. For running user just needs to specify configuration.

First build by maven
## Prerequisites ##

* Kafka broker
* YARN cluster
* samza configuration

Job is possible to deploy through HDFS file system or HTTP server

**1. Prepare package for distribution**
### HDFS ###

User have to run also HDFS file system on his YARN cluster.

1.1 Copy job package to hdfs file system
```
#!python
hadoop fs -put .file://samza-esper-job.tar.gz /path/for/tgz

```
1.2  Edit job configuration, Change the yarn.package.path in the configuration file of job to your HDFS location
```
#!python
yarn.package.path=hdfs://<hdfs name node ip>:<hdfs name node port>/path/to/tgz

```

1.3  Start Esper job from samzaesper dir
```
#!python
bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file:///${PWD}/config/samzaesper.properties

```

how to configure samza job you can find in http://samza.apache.org/learn/documentation/0.7.0/jobs/configuration-table.html

# Author #

Stefan Repcek
repcek2@gmail.com 
https://www.linkedin.com/in/stefanrepcek


