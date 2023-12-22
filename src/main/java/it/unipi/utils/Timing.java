package it.unipi.utils;

import it.unipi.encoding.CompressionType;
import it.unipi.encoding.Tokenizer;
import it.unipi.encoding.implementation.TokenizerImpl;
import it.unipi.scoring.DocumentScore;
import it.unipi.scoring.MaxScore;
import it.unipi.scoring.ScoringType;
import opennlp.tools.parser.Cons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class Timing {

    public static void TimeIT(boolean cache, boolean refresh, int maxRes){
        Constants.CACHING = cache;
        Constants.startSession();
        long start;
        long end;
        long accumulated = 0;
        int queries = 0;
        try(
                BufferedReader readerQ = new BufferedReader(new FileReader("./data/evaluation/queries.train.tsv"));
        ){
            String query;



            while((query = readerQ.readLine()) != null){
                if(refresh)
                    Constants.startSession();
                if(queries++ == maxRes)
                    break;
                start = System.currentTimeMillis();
                MaxScore scorer = new MaxScore(Constants.vocabulary, Constants.documentIndex, new TokenizerImpl(true, true));
                PriorityQueue<DocumentScore> scoring = scorer.score(query.split("\t")[1], 1, "disjunctive");
                end = System.currentTimeMillis();
                accumulated += (end - start);
            }


        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("THROUGHPUT WITH CACHE, REFRESH = " + cache +  "   " +  refresh + " SEARCHING FOR " + maxRes + " QUERIES : " + accumulated);
        //Constants.onExit();
    }

    public static void main(String[] args){
        Constants.setCompression(CompressionType.COMPRESSED);
        Constants.setPath(Path.of("./data"));
        Constants.setScoring(ScoringType.TFIDF);
        long start, end;
        int queries = 0;
/*
        Constants.CACHING = true;
        Constants.startSession();

        try(
                BufferedReader readerQ = new BufferedReader(new FileReader("./data/evaluation/queries.train.tsv"));
                ){
            String query;


            start = System.currentTimeMillis();
            while((query = readerQ.readLine()) != null){
                Tokenizer tok = Tokenizer.getInstance();
                for(String param: tok.tokenizeBySpace(query.split("\t")[1]))
                    Constants.vocabulary.getEntry(param);
                //MaxScore scorer = new MaxScore(Constants.vocabulary, Constants.documentIndex, new TokenizerImpl(true, true));
                //PriorityQueue<DocumentScore> scoring = scorer.score(query.split("\t")[1], 1, "disjunctive");
            }
            end = System.currentTimeMillis();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Constants.onExit();

        System.out.println("LEARNING INFLUENCERS : " + (end - start));

 */

        // ------------------------------------------------------------------------------------------------------------------------------



        System.gc();

   //    TimeIT(false, true, 1);
        //TimeIT(false, true,10);
        //TimeIT(false, true, 100);

    //    TimeIT(true, false,1);
       //TimeIT(true, false,10);
      // TimeIT(true, false, 100);
     // TimeIT(false, false,1);
     //  TimeIT(false, false,10);
      TimeIT(false, false,100);






    }
}
