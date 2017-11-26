package com.roncoo.eshop.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

/**
 * Created by hadoop on 2017/11/26.
 *
 * spout
 * spout，继承一个基类，实现接口，这个里面主要是负责从数据源获取数据
 * 我们这里作为一个简化，就不从外部的数据源去获取数据了，只是自己内部不断发射一些句子
 * @author hadoop
 *
 */
public class RandomSentenceSpout extends BaseRichSpout {

    private static final long serialVersionUID = 3699352201538354417L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomSentenceSpout.class);

    private SpoutOutputCollector collector;
    private Random random;

    /**
     * open方法
     *
     * open方法，是对spout进行初始化的
     *
     * 比如说，创建一个线程池，或者创建一个数据库连接池，或者构造一个httpclient
     *
     */
    @SuppressWarnings("rawtypes")
    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        // 在open方法初始化的时候，会传入进来一个东西，叫做SpoutOutputCollector
        // 这个SpoutOutputCollector就是用来发射数据出去的
        this.collector = collector;
        // 构造一个随机数生产对象
        this.random = new Random();
    }

    /**
     * nextTuple方法
     *
     * 这个spout类，之前说过，最终会运行在task中，某个worker进程的某个executor线程内部的某个task中
     * 那个task会负责去不断的无限循环调用nextTuple()方法
     * 只要的话呢，无限循环调用，可以不断发射最新的数据出去，形成一个数据流
     *
     */
    public void nextTuple() {
        Utils.sleep(100);
        String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
                "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};
        String sentence = sentences[random.nextInt(sentences.length)];
        LOGGER.info("【发射句子】sentence=" + sentence);
        // 这个values，你可以认为就是构建一个tuple
        // tuple是最小的数据单位，无限个tuple组成的流就是一个stream
        collector.emit(new Values(sentence));
    }

    /**
     * declareOutputFielfs这个方法
     *
     * 很重要，这个方法是定义一个你发射出去的每个tuple中的每个field的名称是什么
     *
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
    }

}
