package com.roncoo.eshop.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 写一个bolt，直接继承一个BaseRichBolt基类
 * <p>
 * 实现里面的所有的方法即可，每个bolt代码，同样是发送到worker某个executor的task里面去运行
 *
 * @author Administrator
 */
public class SplitSentence extends BaseRichBolt {

    private static final long serialVersionUID = 6604009953652729483L;

    private OutputCollector collector;

    /**
     * 对于bolt来说，第一个方法，就是prepare方法
     * <p>
     * OutputCollector，这个也是Bolt的这个tuple的发射器
     */
    @SuppressWarnings("rawtypes")
    public void prepare(Map conf, TopologyContext context, OutputCollector collector){
        this.collector = collector;
    }

    /**
     * execute方法
     * <p>
     * 就是说，每次接收到一条数据后，就会交给这个executor方法来执行
     */
    public void execute(Tuple tuple) {
        String sentence = tuple.getStringByField("sentence");
        String[] words = sentence.split(" ");
        for (String word : words) {
            collector.emit(new Values(word));
        }
    }

    /**
     * 定义发射出去的tuple，每个field的名称
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
