package Assignment2;


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

    List<LeafReaderContext> leafReaderContexts;

    public EasySearch(String path) {
        try {
            this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
            this.searcher = new IndexSearcher(reader);
            this.analyzer = new StandardAnalyzer();
            documentLengthMap = new HashMap<String, Double>();
            termFreqPerDocMap = new HashMap<String, Integer>();
            leafReaderContexts = reader.getContext().leaves();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public double calculateRelevanceScore(String queryString) {
        int totalNumberOfDocs = reader.maxDoc();
        System.out.println("totalNumberOfDocs = " + totalNumberOfDocs);
        calculateLengthForAllDocs();


        Set<Term> queryTerms = getQueryTerms(queryString);
        for (Term queryTerm : queryTerms) {
            calculateTermFreqForAllDocs(queryTerm.text());
        }

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
                        tfIDF += calculateTFIDF(totalNumberOfDocs, termFrequencyPerDocument, lengthOfDocument, documentFrequency);
                    }
                    System.out.println("Relevance Score for  " + queryString + " in" + docNo + " is" + tfIDF);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return 0.0;
    }

    private void calculateTermFreqForAllDocs(String term) {
        System.out.println("Making term frequency map for " + term);
        for (LeafReaderContext leafReaderContext : leafReaderContexts) {
            try {
//                int startDoc = leafReaderContext.docBase;
                PostingsEnum postingsEnum = MultiFields.getTermDocsEnum(leafReaderContext.reader(), "TEXT", new BytesRef(term));
                int doc;
                if (postingsEnum != null) {
                    while ((doc = postingsEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                        System.out.println(postingsEnum.docID());
                        String docNo = searcher.doc(postingsEnum.docID()).get("DOCNO");
                        termFreqPerDocMap.put(docNo + term, postingsEnum.freq());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    private void calculateLengthForAllDocs() {
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
        return ((double) countOfTermPerDocument / documentLength) * Math.log(1 + totalNumberOfDocuments / termFrequency);
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
            Query query = parser.parse(queryString);
            searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
            System.out.println("Terms in query are ");
            for (Term queryTerm : queryTerms) {
                System.out.println("queryTerm.text() = " + queryTerm.text());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryTerms;
    }
}