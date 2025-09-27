package com.test;

import lombok.Builder;
import lombok.Data;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.util.VMSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/*

*/
@Data
@Builder
class FlyWeighCharacter{
   char ch;
   String fontName;
   int fontSize;
   boolean isBold;
   boolean isItalic;

   public String getStyle(){
       return ch+"-"+fontName+"-"+fontSize+(isBold ? "-b":"")+(isItalic ? "-i":"");
   }
}

class FlyWeighCharacterFactory{
    private Map<String,FlyWeighCharacter> characterMap = new HashMap<>();

    public FlyWeighCharacter getFlyWeightCharacter(String style){
        if(characterMap.containsKey(style)){
            return characterMap.get(style);
        }
        String []attr = style.split("-");
        char ch = attr[0].charAt(0);
        boolean isBold = false;
        boolean isItalic = false;
        if(attr.length==4) {
            isBold = attr[3].equals("b") ? true : false;
            isItalic = attr[3].equals("i")? true : false;
        }
        if(attr.length==5) {
            isBold = attr[3].equals("b") ? true : false;
            isItalic = attr[4].equals("i")? true : false;
        }
        FlyWeighCharacter character = FlyWeighCharacter.builder().ch(ch).fontName(attr[1]).fontSize(Integer.parseInt(attr[2])).isBold(isBold).isItalic(isItalic).build();
        characterMap.put(style,character);
        return character;
    }
}

class DocumentRow {
    private List<FlyWeighCharacter> rowCharacters = new ArrayList<>();

    public void addCharacter(FlyWeighCharacter ch,int col){
        rowCharacters.add(ch);
        int current = rowCharacters.size()-1;
        while(current>0 && current>col){
            FlyWeighCharacter temp = rowCharacters.get(current-1);
            rowCharacters.set(current-1,rowCharacters.get(current));
            rowCharacters.set(current,temp);
            current--;
        }
    }

    public boolean removeCharacter(int col){
        int size = rowCharacters.size();
        if(col<0 || col>=size)
            return false;
        rowCharacters.remove(col);
        return true;
    }

    public String readLine(){
        StringBuilder builder = new StringBuilder();
        for(FlyWeighCharacter ch:rowCharacters){
            builder.append(ch.getCh());
        }
        return builder.toString();
    }

    public String getStyle(int col){
        if(rowCharacters.size()<col){
            return "";
        }
        return rowCharacters.get(col).getStyle();
    }
}

class WordDocumentProcessor{
    private List<DocumentRow> rows = new ArrayList<>();
    FlyWeighCharacterFactory flyWeighCharacterFactory = new FlyWeighCharacterFactory();


    public void addCharacter(int row,int col,char ch,String fontName,int fontSize,boolean isBold,boolean isItalic){
        while(row > rows.size()-1){
           rows.add(new DocumentRow());
        }
        DocumentRow documentRow = rows.get(row);
        String key =  ch+"-"+fontName+"-"+fontSize+(isBold ? "-b":"")+(isItalic ? "-i":"");
        FlyWeighCharacter character = flyWeighCharacterFactory.getFlyWeightCharacter(key);
        documentRow.addCharacter(character,col);
    }

    public boolean removeCharacter(int row,int col){
        if(rows.size()<row)
            return false;
        return rows.get(row).removeCharacter(col);
    }

    public String readLine(int row){
        if(rows.size()<row){
            return "";
        }
        return rows.get(row).readLine();
    }

    public String getStyle(int row,int col){
        if(rows.size()<row)
            return "";
        return rows.get(row).getStyle(col);
    }

}



public class WordProcessor {

    public static void main(String args[]){
        WordDocumentProcessor obj= new WordDocumentProcessor();
        obj.addCharacter(0, 0, 'g', "Cambria", 17, true, true);
        obj.addCharacter(1, 0, 'y', "Century Gothic", 14, true, true);
        obj.addCharacter(1, 1, 'h', "Courier New", 22, false, false);
        obj.addCharacter(1, 2, 'y', "Georgia", 14, false, false);

        System.out.println(obj.getStyle(0,0));
        System.out.println(obj.readLine(0));
        obj.addCharacter(0, 0, 'q', "Arial", 21, false, true);
        System.out.println(obj.readLine(0));

        System.out.println(obj.readLine(1));
        obj.removeCharacter(1, 1);
        System.out.println( obj.readLine(1));
        obj.removeCharacter(1, 4);
    }

}
