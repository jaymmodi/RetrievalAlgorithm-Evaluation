package Assignment2;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EasySearch {

    public IndexReader reader;
    public IndexSearcher searcher;
    public Analyzer analyzer;

    public EasySearch(String path) {
        try {
            this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
            this.searcher = new IndexSearcher(reader);
            this.analyzer = new StandardAnalyzer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public double calculateRelevanceScore(String queryString) {

        Set<Term> queryTerms = getQueryTerms(queryString);
        for (Term queryTerm : queryTerms) {
            int documentFreq = getDocumentFrequency(queryTerm);
            System.out.println(documentFreq);
        }

        int normalizedDocumentLength = getNormalizedDocumentLength();


        return 0.0;
    }

    private int getNormalizedDocumentLength() {
        DefaultSimilarity defaultSimilarity = new DefaultSimilarity();
        List<LeafReaderContext> leafReaderContexts = reader.getContext().leaves();

        for (LeafReaderContext leafReaderContext : leafReaderContexts) {
            int startDocNo = leafReaderContext.docBase;
            int numberOfDoc = leafReaderContext.reader().maxDoc();
            for (int docId = 0; docId < numberOfDoc; docId++) {

                float normDocLeng = 0;
                try {
                    normDocLeng = defaultSimilarity.decodeNormValue(leafReaderContext.reader().getNormValues("TEXT").get(docId));
                    // Get length of the document
                    float docLeng = 1 / (normDocLeng * normDocLeng);
                    System.out.println("Length of doc(" + (docId +
                            startDocNo)
                            + ", " + searcher.doc(docId +
                            startDocNo).get("DOCNO")
                            + ") is " + docLeng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
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