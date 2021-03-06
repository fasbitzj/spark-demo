package com.ucl.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jiang.zheng on 2017/11/9.
 */
public class App {
    public static void main(String[] args) {
        List<Applicant> inputData = Arrays.asList(
                new Applicant(1, "John", "Doe", 10000, 568),
                new Applicant(2, "John", "Greg", 12000, 654),
                new Applicant(3, "Mary", "Sue", 100, 568),
                new Applicant(4, "Greg", "Darcy", 1000000, 788),
                new Applicant(5, "Jane", "Stuart", 10, 788)
        );

        SparkConf conf = new SparkConf().setMaster("local").setAppName("Simple Application");
//        SparkConf conf = new SparkConf().setMaster("spark://10.1.210.50:7077").setAppName("NetworkWordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<Applicant> applicants = sc.parallelize(inputData);

        KieBase rules = loadRules();
        Broadcast<KieBase> broadcastRules = sc.broadcast(rules);
        long numApproved = applicants.map( a -> applyRules(broadcastRules.value(), a) )
                .filter(a -> a.isApproved())
                .count();

        /*for (int i=0; i<inputData.size(); i++) {
            applyRules(broadcastRules.value(), inputData.get(i));
            System.out.println(inputData.get(i));
        }*/

        System.out.println("Number of applicants approved: " + numApproved);
    }

    public static KieBase loadRules() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        return kieContainer.getKieBase();
    }

    public static Applicant applyRules(KieBase base, Applicant a) {
        StatelessKieSession session = base.newStatelessKieSession();
        session.execute(CommandFactory.newInsert(a));
        return a;
    }

    /*public static KieContainer loadRules() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        return kContainer;
    }

    public static Applicant applyRules(KieContainer kContainer, Applicant a) {
        StatelessKieSession kSession = kContainer.newStatelessKieSession();
        kSession.execute(a);
        return a;
    }*/
}
