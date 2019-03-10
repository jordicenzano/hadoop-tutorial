package hadoop;

import java.util.*;
import java.io.Console;
import java.io.IOException;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class WarmDays {
    // Mapper class
    public static class E_EMapper extends MapReduceBase implements Mapper<LongWritable, /* Input key Type */
            Text, /* Input value Type */
            Text, /* Output key Type */
            IntWritable> /* Output value Type */
    {
        // Map function
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output,

            Reporter reporter) throws IOException {
            String line = value.toString();
            String[] s = line.split(",");

            String strDate = "NA";
            int tempF = Integer.MIN_VALUE;

            if (s.length > 1) { // Date
                try {
                    String[] dateData = s[1].substring(1, s[1].length() - 1).split("-");
                    if (dateData.length > 2) {
                        strDate = dateData[0].substring(0,4) + dateData[1].substring(0,2) + dateData[2].substring(0,2);
                    }
                }
                catch(StringIndexOutOfBoundsException e) {
                    //couldn't compose
                    strDate = "ERR";
                }
            }

            if (s.length > 43) { // dry-bulb temperature (DBT)
                try {
                    tempF = Integer.parseInt(s[43].substring(1, s[43].length() - 1));
                }
                catch(Exception e) {
                    //couldn't parse
                    tempF = -1;
                }
            }

            output.collect(new Text(strDate), new IntWritable(tempF));
        }
    }

    // Reducer class
    public static class E_EReduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        // Reduce function
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
                Reporter reporter) throws IOException {
            int minTemp = 75;
            int val = Integer.MIN_VALUE;

            while (values.hasNext()) {
                val = values.next().get();
                if (val > minTemp) {
                    output.collect(key, new IntWritable(val));
                }
            }
        }
    }

    // Main function
    public static void main(String args[]) throws Exception {
        JobConf conf = new JobConf(WarmDays.class);

        conf.setJobName("warm_days");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        conf.setMapperClass(E_EMapper.class);
        conf.setCombinerClass(E_EReduce.class);
        conf.setReducerClass(E_EReduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }
}
