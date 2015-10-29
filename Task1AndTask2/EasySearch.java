package Assignment2.Task1AndTask2;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class EasySearch {

    public IndexReader reader;
    public IndexSearcher searcher;
    public Analyzer analyzer;
    public Map<String, Double> documentLengthMap;
    public Map<String, Integer> termFreqPerDocMap;
    public PriorityQueue<QueryScore> top1000Queue;
    public DefaultSimilarity defaultSimilarity;

    List<LeafReaderContext> leafReaderContexts;

    public EasySearch(String path) {
        try {
            this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
            this.searcher = new IndexSearcher(reader);
            this.analyzer = new StandardAnalyzer();
            this.documentLengthMap = new HashMap<>();
            this.termFreqPerDocMap = new HashMap<>();
            this.leafReaderContexts = reader.getContext().leaves();
            this.top1000Queue = new PriorityQueue<>(1000, new QueryScore());
            this.defaultSimilarity = new DefaultSimilarity();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public PriorityQueue<QueryScore> calculateRelevanceScore(String queryString, int id) {
        int totalNumberOfDocs = reader.maxDoc();
        termFreqPerDocMap.clear();
        top1000Queue.clear();
        Set<Term> queryTerms = createFrequencyMapForAllTerms(queryString);

        for (LeafReaderContext leafReaderContext : leafReaderContexts) {
            int startDoc = leafReaderContext.docBase;
            int numberDocs = leafReaderContext.reader().maxDoc();

            for (int docId = 0; docId < numberDocs; docId++) {
                try {
                    String docNo = searcher.doc(docId + startDoc).get("DOCNO");
                    double tfIDF = 0.0;
                    int termFrequencyPerDocument;
                    for (Term term : queryTerms) {
                        if (termFreqPerDocMap.containsKey(docNo + term.text())) {
                            termFrequencyPerDocument = termFreqPerDocMap.get(docNo + term.text());
                        } else {
                            termFrequencyPerDocument = 0;
                        }
                        double lengthOfDocument = documentLengthMap.get(docNo);
                        int documentFrequency = getDocumentFrequency(term);
                        if (documentFrequency != 0) {
                            tfIDF += calculateTFIDF(totalNumberOfDocs, termFrequencyPerDocument, lengthOfDocument, documentFrequency);
                        }
                    }
                    if (tfIDF != 0.0) {
                        QueryScore queryScore = storeInQueryStoreObject(queryString, docNo, tfIDF, id);
                        addInPriorityQueue(queryScore);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return top1000Queue;
    }

    private void addInPriorityQueue(QueryScore queryScore) {
        if (top1000Queue.size() < 1000) {
            top1000Queue.add(queryScore);
        } else {
            if (top1000Queue.peek().getScore() < queryScore.getScore()) {
                top1000Queue.remove();
                top1000Queue.add(queryScore);
            }
        }
    }

    private QueryScore storeInQueryStoreObject(String queryString, String docNo, double tfIDF, int id) {
        QueryScore queryScore = new QueryScore();
        queryScore.setQuery(queryString);
        queryScore.setDocNo(docNo);
        queryScore.setScore(tfIDF);
        queryScore.setId(id);
        return queryScore;
    }

    private Set<Term> createFrequencyMapForAllTerms(String queryString) {
        Set<Term> queryTerms = getQueryTerms(queryString);
        for (Term queryTerm : queryTerms) {
            calculateTermFreqForAllDocs(queryTerm.text());
        }
        return queryTerms;
    }

    private void calculateTermFreqForAllDocs(String term) {
        for (LeafReaderContext leafReaderContext : leafReaderContexts) {
            try {
                PostingsEnum postingsEnum = MultiFields.getTermDocsEnum(leafReaderContext.reader(), "TEXT", new BytesRef(term));
                int startDoc = leafReaderContext.docBase;
                int doc;
                if (postingsEnum != null) {
                    while ((doc = postingsEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                        String docNo = searcher.doc(postingsEnum.docID() + startDoc).get("DOCNO");
                        termFreqPerDocMap.put(docNo + term, postingsEnum.freq());
                    }
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    public void calculateLengthForAllDocs() {
        DefaultSimilarity defaultSimilarity = new DefaultSimilarity();

        for (LeafReaderContext leafReaderContext : leafReaderContexts) {
            int startDoc = leafReaderContext.docBase;
            int numberOfDocs = leafReaderContext.reader().maxDoc();

            double normDocLength;
            for (int docId = 0; docId < numberOfDocs; docId++) {
                try {
                    normDocLength = defaultSimilarity.decodeNormValue(leafReaderContext.reader().getNormValues("TEXT").get(docId));

                    double docLength = 1 / (normDocLength * normDocLength);

                    String docNo = searcher.doc(docId + startDoc).get("DOCNO");
                    documentLengthMap.put(docNo, docLength);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public double calculateTFIDF(int totalNumberOfDocuments, int countOfTermPerDocument, double documentLength, int termFrequency) {
        return ((double) countOfTermPerDocument / documentLength) * Math.log10(1 + (double) totalNumberOfDocuments / termFrequency);
    }


    private int getDocumentFrequency(Term queryTerm) {
        int freq = 0;
        try {
            freq = reader.docFreq(queryTerm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freq;
    }

    private Set<Term> getQueryTerms(String queryString) {
        QueryParser parser = new QueryParser("TEXT", analyzer);
        Set<Term> queryTerms = new LinkedHashSet<Term>();
        try {
            Query query = parser.parse(QueryParser.escape(queryString));
            searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return queryTerms;
    }
}