package com.roncoo.eshop.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
/**
 * 单词计数拓扑
 * 
 * 我认识很多java工程师，都是会一些大数据的技术的，不会太精通，没有那么多的时间去研究
 * storm的课程，我就只是讲到，最基本的开发，就够了，java开发广告计费系统，大量的流量的引入和接入，就是用storm做得
 * 用storm，主要是用它的成熟的稳定的易于扩容的分布式系统的特性
 * java工程师，来说，做一些简单的storm开发，掌握到这个程度差不多就够了
 * 
 * @author Administrator
 *
 */
public class WordCountTopology {
	
	public static void main(String[] args) {
		// 在main方法中，会去将spout和bolts组合起来，构建成一个拓扑
		TopologyBuilder builder = new TopologyBuilder();
	
		// 这里的第一个参数的意思，就是给这个spout设置一个名字
		// 第二个参数的意思，就是创建一个spout的对象
		// 第三个参数的意思，就是设置spout的executor有几个,线程数
		builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2);
		builder.setBolt("SplitSentence", new SplitSentence(), 5)
				.setNumTasks(10)
				.shuffleGrouping("RandomSentence");
		// 这个很重要，就是说，相同的单词，从SplitSentence发射出来时，一定会进入到下游的指定的同一个task中
		// 只有这样子，才能准确的统计出每个单词的数量
		// 比如你有个单词，hello，下游task1接收到3个hello，task2接收到2个hello
		// 5个hello，全都进入一个task
		builder.setBolt("WordCount", new WordCount(), 10)
				.setNumTasks(20)
				.fieldsGrouping("SplitSentence", new Fields("word"));  
		
		Config config = new Config();
	
		// 说明是在命令行执行，打算提交到storm集群上去
		if(args != null && args.length > 0) {
			config.setNumWorkers(3);  
			try {
				StormSubmitter.submitTopology(args[0], config, builder.createTopology());  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 说明是在eclipse里面本地运行
			config.setMaxTaskParallelism(20);  
			
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("WordCountTopology", config, builder.createTopology());  
			
			Utils.sleep(60000); 
			
			cluster.shutdown();
		}
	}
	
}
