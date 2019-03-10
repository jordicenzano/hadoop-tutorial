# HADOOP TUTORIAL
The idea of this repo is to provide some simple step by step guide to set up a test version Hadoop and start playing around with it :-).
This set up is totally NOT recommended at all for production workloads.

## Set up hadhoop locally
Because our purpose is just to experiment, to accelerate the set up we could use a docker image, so the first step is to pull that image(*):
```
docker pull sequenceiq/hadoop-docker:2.7.1
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
That file contains the climathological data from a wheather station in hourly basis for all 2018.

## Analyzing data
The first step to use mapreduce to analyze some data is to put the data file inside Hadoop (in HDFS), to do that first will execute the Hadhoop container sharing the project dir:
```
docker run -it --rm -p8088:8088 -p8042:8042 -p50070:50070 -v $(pwd):/usr/local/hadoop/host_shared sequenceiq/hadoop-docker:2.7.1 /etc/bootstrap.sh -bash
```
### Preparing input and output
Add the file to HDFS, inside the container shell type:
```
cd $HADOOP_PREFIX
./bin/hadoop fs -put host_shared/data/1647211.csv input/example-temperature.csv
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
javac -cp $(./bin/hadoop classpath) -d host_shared/mapreduce/class host_shared/mapreduce/java/WarmDays.java
```
The previous command will generate class files, now we need to create the jar by doing:
```
jar -cvf host_shared/mapreduce/jar/WarmDays.jar -C host_shared/mapreduce/class/ .
```
This will create the file `mapreduce/jar/WarmDays.jar`

### Running the job
The file `mapreduce/jar/WarmDays.jar` defines the Hadoop job to execute. To run the job we have to:
```
./bin/hadoop jar host_shared/mapreduce/jar/WarmDays.jar hadoop.WarmDays input/example-temperature.csv output
```
### Checking the output
We can take a look to the output file doing:
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

## References
- Hadoop docker image: https://github.com/sequenceiq/hadoop-docker
- Hadhoop tutorial: https://www.tutorialspoint.com/hadoop/hadoop_mapreduce.htm