package marytts.data;


import java.util.ArrayList;

/* XML */
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class XML2Data
{
    private static ArrayList<Object> analyzeXML(Element elt)
    {
        
        ArrayList<Object> list = new ArrayList<Object>();
	    final NodeList children = elt.getChildNodes();
	    final int nbChildren = children.getLength();
        
        if (elt.getNodeName().equals("phrase"))
        {
            String tone = "";
            int break_index = -1;
            int end_pause_duration = 0;
            ArrayList<Token> tokens = new ArrayList<Token>();
            for (int i = 0; i<nbChildren; i++) {
                
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    if (children.item(i).getNodeName().equals("boundary")) // FIXME: patch to deal with boundaries
                    {
                        Element bnd = (Element) children.item(i);
                        tone = bnd.getAttribute("tone");
                        break_index = Integer.parseInt(bnd.getAttribute("breakindex"));
                        end_pause_duration = Integer.parseInt(bnd.getAttribute("duration"));
                    }
                    else
                    {
                        for (Object o:analyzeXML((Element) children.item(i)))
                        {
                            tokens.add((Token) o);
                        }
                    }
                }
            }
            
            Phrase p;
            if (end_pause_duration == 0)
            {
                p = new Phrase(tokens);
            }
            else
            {
                
                p = new Phrase(tokens, break_index, end_pause_duration, tone);
            }
            list.add(p);
        }
        else if (elt.getNodeName().equals("t"))
        {
            ArrayList<Syllable> syllables = new ArrayList<Syllable>();
            for (int i = 0; i<nbChildren; i++) {
                if(children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    for (Object o:analyzeXML((Element) children.item(i)))
                    {
                        syllables.add((Syllable) o);
                    }
                }
            }
            Token w = new Token(syllables, elt.getAttribute("pos"));
            list.add(w);
        }
        else if (elt.getNodeName().equals("syllable"))
        {
            ArrayList<Phone> phones = new ArrayList<Phone>();
            for (int i = 0; i<nbChildren; i++) {
                if(children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    for (Object o:analyzeXML((Element) children.item(i)))
                    {
                        phones.add((Phone) o);
                    }
                }
            }
            Syllable s;
            if (elt.getAttribute("stress").equals("1"))
                s = new Syllable(phones, true);
            else
                s = new Syllable(phones, false);
            list.add(s);
        }
        else if (elt.getNodeName().equals("ph"))
        {
            list.add(new Phone(elt.getAttribute("p"), Integer.parseInt(elt.getAttribute("d"))));
        }
        else
        {
            for (int i = 0; i<nbChildren; i++) {
                if(children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    list.addAll(analyzeXML((Element) children.item(i)));
                }
            }

        }

        return list;
    }
    
    public static Sample convertXML(Document document)
    {
        /*
	     * Etape 4 : récupération de l'Element racine
	     */
	    final Element racine = document.getDocumentElement();

        ArrayList<Phrase> phrases = new ArrayList<Phrase>();
        for (Object o:analyzeXML(racine))
        {
            phrases.add((Phrase) o);
        }
        return new Sample(phrases);
    }
}
