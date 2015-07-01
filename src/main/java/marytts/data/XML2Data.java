package marytts.data;


import java.util.ArrayList;

/* XML */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
            ArrayList<Token> tokens = new ArrayList<Token>();
            for (int i = 0; i<nbChildren; i++) {
                if(children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    for (Object o:analyzeXML((Element) children.item(i)))
                    {
                        tokens.add((Token) o);
                    }
                }
            }
            Phrase p = new Phrase(tokens);
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
	    // for (int i = 0; i<nbRacineNoeuds; i++) {
	    //     if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	    //         final Element personne = (Element) racineNoeuds.item(i);
				
        //         //Affichage d'une personne
        //         System.out.println("\n*************PERSONNE************");
        //         System.out.println("sexe : " + personne.getAttribute("sexe"));
                
	    // 	    /*
        //          * Etape 6 : récupération du nom et du prénom
        //          */
        //         final Element nom = (Element) personne.getElementsByTagName("nom").item(0);
        //         final Element prenom = (Element) personne.getElementsByTagName("prenom").item(0);
                
        //         //Affichage du nom et du prénom
        //         System.out.println("nom : " + nom.getTextContent());
        //         System.out.println("prénom : " + prenom.getTextContent());
                
        //         /*
        //          * Etape 7 : récupération des numéros de téléphone
        //          */
        //         final NodeList telephones = personne.getElementsByTagName("telephone");
        //         final int nbTelephonesElements = telephones.getLength();
                
        //         for(int j = 0; j<nbTelephonesElements; j++) {
        //             final Element telephone = (Element) telephones.item(j);
                    
        //             //Affichage du téléphone
        //             System.out.println(telephone.getAttribute("type") + " : " + telephone.getTextContent());
        //         }			
        //     }			
        // }
        // catch (final ParserConfigurationException e) {
        //     e.printStackTrace();
        // }
        // catch (final SAXException e) {
        //     e.printStackTrace();
        // }
        // catch (final IOException e) {
        //     e.printStackTrace();
        // }		
    }
}
