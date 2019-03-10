# HADOOP TUTORIAL
The idea of this repo is to provide some simple step by step guide to set up a an isolated test/dev version Hadoop locally and start playing around with it :-).
This set up is totally NOT recommended for production workloads.

Note: As I mentioned in [References](#References) this work is based in the containers build by [Big Data Europe](https://github.com/big-data-europe)

## Set up Hadhoop locally (isolated)
Because our purpose is just to experiment, to accelerate the set up we could use a docker images, so the first step is to pull the docker images and create the cluster based on those images.
To do that we'll execute the following command from the root of this project(*):
```
docker-compose up 
```
(*) We are assuming that you have docker installed and configured, if not take a look to this [guide](https://docs.docker.com/install/overview/)

## Getting some data to analyze
You can download some data files from here https://www.noaa.gov/
In our example we'll be analyzing the following file [1647211.csv](./data/1647211.csv), here you can see a sample:
```
"STATION","DATE","REPORT_TYPE","SOURCE","AWND","BackupDirection","BackupDistance","BackupDistanceUnit","BackupElements","BackupElevation","BackupElevationUnit","BackupEquipment","BackupLatitude","BackupLongitude","BackupName","CDSD","CLDD","DSNW","DailyAverageDewPointTemperature","DailyAverageDryBulbTemperature","DailyAverageRelativeHumidity","DailyAverageSeaLevelPressure","DailyAverageStationPressure","DailyAverageWetBulbTemperature","DailyAverageWindSpeed","DailyCoolingDegreeDays","DailyDepartureFromNormalAverageTemperature","DailyHeatingDegreeDays","DailyMaximumDryBulbTemperature","DailyMinimumDryBulbTemperature","DailyPeakWindDirection","DailyPeakWindSpeed","DailyPrecipitation","DailySnowDepth","DailySnowfall","DailySustainedWindDirection","DailySustainedWindSpeed","DailyWeather","HDSD","HTDD","HeavyFog","HourlyAltimeterSetting","HourlyDewPointTemperature","HourlyDryBulbTemperature","HourlyPrecipitation","HourlyPresentWeatherType","HourlyPressureChange","HourlyPressureTendency","HourlyRelativeHumidity","HourlySeaLevelPressure","HourlySkyConditions","HourlyStationPressure","HourlyVisibility","HourlyWetBulbTemperature","HourlyWindDirection","HourlyWindGustSpeed","HourlyWindSpeed","MonthlyAverageRH","MonthlyDaysWithGT001Precip","MonthlyDaysWithGT010Precip","MonthlyDaysWithGT32Temp","MonthlyDaysWithGT90Temp","MonthlyDaysWithLT0Temp","MonthlyDaysWithLT32Temp","MonthlyDepartureFromNormalAverageTemperature","MonthlyDepartureFromNormalCoolingDegreeDays","MonthlyDepartureFromNormalHeatingDegreeDays","MonthlyDepartureFromNormalMaximumTemperature","MonthlyDepartureFromNormalMinimumTemperature","MonthlyDepartureFromNormalPrecipitation","MonthlyDewpointTemperature","MonthlyGreatestPrecip","MonthlyGreatestPrecipDate","MonthlyGreatestSnowDepth","MonthlyGreatestSnowDepthDate","MonthlyGreatestSnowfall","MonthlyGreatestSnowfallDate","MonthlyMaxSeaLevelPressureValue","MonthlyMaxSeaLevelPressureValueDate","MonthlyMaxSeaLevelPressureValueTime","MonthlyMaximumTemperature","MonthlyMeanTemperature","MonthlyMinSeaLevelPressureValue","MonthlyMinSeaLevelPressureValueDate","MonthlyMinSeaLevelPressureValueTime","MonthlyMinimumTemperature","MonthlySeaLevelPressure","MonthlyStationPressure","MonthlyTotalLiquidPrecipitation","MonthlyTotalSnowfall","MonthlyWetBulb","NormalsCoolingDegreeDay","NormalsHeatingDegreeDay","REM","REPORT_TYPE","SOURCE","ShortDurationEndDate005","ShortDurationEndDate010","ShortDurationEndDate015","ShortDurationEndDate020","ShortDurationEndDate030","ShortDurationEndDate045","ShortDurationEndDate060","ShortDurationEndDate080","ShortDurationEndDate100","ShortDurationEndDate120","ShortDurationEndDate150","ShortDurationEndDate180","ShortDurationPrecipitationValue005","ShortDurationPrecipitationValue010","ShortDurationPrecipitationValue015","ShortDurationPrecipitationValue020","ShortDurationPrecipitationValue030","ShortDurationPrecipitationValue045","ShortDurationPrecipitationValue060","ShortDurationPrecipitationValue080","ShortDurationPrecipitationValue100","ShortDurationPrecipitationValue120","ShortDurationPrecipitationValue150","ShortDurationPrecipitationValue180","Sunrise","Sunset","TStorms","WindEquipmentChangeDate"
"08449013025","2018-01-01T01:00:00","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"30.53","48","52",,,,,"88",,,,,,"010",,"6",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"MET051METAR LERT 010000Z 01005KT CAVOK 11/09 Q1034 NOSIG=","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,
"08449013025","2018-01-01T02:00:00","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"30.50","48","50",,,,,"94",,"FEW:02 40",,"6.21",,"050",,"3",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"MET057METAR LERT 010100Z 05003KT 9999 FEW040 10/09 Q1033 NOSIG=","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,
"08449013025","2018-01-01T03:00:00","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"30.50","48","48",,,,,"100",,,,,,"030",,"6",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"MET051METAR LERT 010200Z 03005KT CAVOK 09/09 Q1033 NOSIG=","FM-15","4",,,,,,,,,,,,,,,,,,,,,,,,,,,,
...
```
That file contains the climatological data from a weather station in hourly basis for all 2018.

## Analyzing data
The first step to use mapreduce to analyze some data is to put the data file inside Hadoop (in HDFS), to do that first will execute the Hadhoop container:
```
docker-compose up
```
### Preparing input and output
Let's open a shell in `resourcemanager`, we have a share volume with the host in this container
```
docker-compose exec resourcemanager bash
```
Now from that shell we will add the file to HDFS:
```
# hdfs dfs -mkdir /input
# hdfs dfs -put ./hadoop/host_shared/data/1647211.csv /input/example-temperature.csv
```
Now the file is "inside" Hadoop (HDFS).

### Map function (map reduce)
Next step is to write our map function, in that java code we need to tell mapreducer those 3 things (at least):
- How to parse the input file, see [map function](./mapreduce/java/WarmDays.java#L22) function
- How to reduce the data, in our case just remove all hours / days with temperatures lower or equal than 75 Fahrenheit degrees. See [reduce function](./mapreduce/java/WarmDays.java#L51)
- Finally how to create (syntax) the output file, see [output.collect in reduce function](./mapreduce/java/WarmDays.java#L58)

### Compiling our map function
Once we have our code (file [WarmDays.java](./mapreduce/java/WarmDays.java)), we need to compile it (create the jar) using Hadoop libraries, you can do that executing this from inside the container:
```
# javac -cp $(hadoop classpath) -d hadoop/host_shared/mapreduce/class hadoop/host_shared/mapreduce/java/WarmDays.java
# jar -cvf hadoop/host_shared/mapreduce/jar/WarmDays.jar -C hadoop/host_shared/mapreduce/class/ .
```
This will create the file `mapreduce/jar/WarmDays.jar`

### Running the job
The file `mapreduce/jar/WarmDays.jar` defines the Hadoop job to execute. To run the job we have to:
```
hadoop jar hadoop/host_shared/mapreduce/jar/WarmDays.jar hadoop.WarmDays input/example-temperature.csv output
```
### Checking the output
We can take a look to the output file doing:
```
hdfs dfs -cat /output/part-00000
```
```
...
20180617        79
20180617        81
20180618        77
20180618        79
20180618        91
20180618        90
20180618        86
20180618        82
20180618        77
20180618        79
...
```

### Clean up
Stop the docker containers by doing
```
docker-compose down
```

## References
- Hadoop docker image: https://github.com/big-data-europe/docker-hadoop
- Hadoop tutorial: https://www.tutorialspoint.com/hadoop/hadoop_mapreduce.htm