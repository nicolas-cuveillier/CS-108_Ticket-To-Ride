package ch.epfl.tchu.gui;

import javafx.scene.Node;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
abstract class DecksViewCreator {
    public Node createHandView(ObservableGameState observableGameState){
        return null;
    }

    //deux propriétés : la première contient celui gérant le tirage de billets, la seconde contient celui gérant le tirage de cartes.
    public Node createCardsView(ObservableGameState observableGameState){
        return null;
    }
}
