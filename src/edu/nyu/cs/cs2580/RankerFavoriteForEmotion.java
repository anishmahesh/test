package edu.nyu.cs.cs2580;

import java.util.*;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * Created by naman on 12/6/2016.
 */
public class RankerFavoriteForEmotion extends Ranker{
    public RankerFavoriteForEmotion(Options options,
                                    CgiArguments arguments, Indexer indexer) {
        super(options, arguments, indexer);
        System.out.println("Using Ranker: " + this.getClass().getSimpleName());
    }

    @Override
    public Vector<ScoredDocument> runQuery(Query query, int numResults) {
        Queue<ScoredDocument> rankQueue = new PriorityQueue<ScoredDocument>();
        int threshold = Integer.parseInt(_options._postingThreshold);
        int multiplier = Integer.parseInt(_options._multiplierForThreshold);
        int endIndex = threshold * multiplier;
        int docFetched = 0;
        HashSet<Document> match = new HashSet<>();
        Vector<ScoredDocument> intermediateResult = new Vector<>();

        while(true) {
            NextDoc nextDoc = _indexer.nextDocForEmotion(query, endIndex);
            if (nextDoc.documnets != null) {
                for (Document doc : nextDoc.documnets) {
                    docFetched++;
                    rankQueue.add(scoreDocument(doc, query));
                    if (rankQueue.size() > (numResults * query._pagination) + 1) {
                        rankQueue.poll();
                    }
                }

                if (docFetched > query._pagination * numResults) {
                    CgiArguments.moreDocFlag = true;
                } else {
                    CgiArguments.moreDocFlag = false;
                }

                Vector<ScoredDocument> results = new Vector<ScoredDocument>();
                ScoredDocument scoredDoc = null;
                while ((scoredDoc = rankQueue.poll()) != null) {
                    if (!match.contains(scoredDoc._doc)) {
                        match.add(scoredDoc._doc);
                        results.add(scoredDoc);
                    }
                }

                Collections.sort(results, Collections.reverseOrder());

                for (int i = 0; i <= numResults * query._pagination && i < results.size(); i++) {
                    intermediateResult.add(results.get(i));
                }

                if (intermediateResult.size() >= (numResults * query._pagination) + 1 || nextDoc.stopRepeat == true) {
                    break;
                } else {
                    endIndex = threshold * ++multiplier;
                }
            } else {
                if (intermediateResult.size() >= (numResults * query._pagination) + 1 || nextDoc.stopRepeat == true) {
                    break;
                } else {
                    endIndex = threshold * ++multiplier;
                }
            }
        }

        Vector<ScoredDocument> finalResults = new Vector<>();
        for(int i = (query._pagination -1) * numResults; i< query._pagination *numResults && i<intermediateResult.size(); i++ ) {
            finalResults.add(intermediateResult.get(i));
        }
        return finalResults;
    }

    /*
      Scores Document based on Query Likelyhood probability
   */
    private ScoredDocument scoreDocument(Document doc, Query query) {


        double queryLikelyhoodProbability = 1.0;
        double totalTermsInDoc = ((DocumentIndexed)doc).getTotalTerms();
        double totalTermsInCourpus = _indexer.totalTermFrequency();
        double lambda = 0.5;

        for(String queryToken : query._tokens){
            double termFrequency = _indexer.documentTermFrequency(queryToken,doc._docid, query._emotionType);
            double corpusTermFrequency = _indexer.corpusDocFrequencyByTerm(queryToken, query._emotionType);
            queryLikelyhoodProbability *= (1-lambda)*(termFrequency/totalTermsInDoc)+(lambda)*(corpusTermFrequency/totalTermsInCourpus);
        }

        if (query instanceof QueryPhrase) {
            for (Vector<String> phraseTokens : ((QueryPhrase) query)._phraseTokens) {
                for(String queryToken : phraseTokens){
                    double termFrequency = _indexer.documentTermFrequency(queryToken,doc._docid, query._emotionType);
                    double corpusTermFrequency = _indexer.corpusDocFrequencyByTerm(queryToken, query._emotionType);
                    queryLikelyhoodProbability *= (1-lambda)*(termFrequency/totalTermsInDoc)+(lambda)*(corpusTermFrequency/totalTermsInCourpus);
                }
            }
        }
        //Adding weightage to the title
        String[] tokens = doc.getTitle().toLowerCase().split(" ");
        System.out.println();

        for(String queryToken : query._tokens){
            if(Arrays.asList(tokens).contains(queryToken.toLowerCase())){
                queryLikelyhoodProbability += 0.01;
            }
        }
        return new ScoredDocument(doc, queryLikelyhoodProbability);
    }

}
