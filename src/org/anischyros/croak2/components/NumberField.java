package org.anischyros.croak2.components;

import javax.swing.text.*;

public class NumberField extends UndoableTextField
{
    public NumberField(int n)
    {
        super(n);
        ((PlainDocument) getDocument()).setDocumentFilter(
            new CustomDocumentFilter());
    }

    public NumberField()
    {
        super();
        ((PlainDocument) getDocument()).setDocumentFilter(
            new CustomDocumentFilter());
    }

    public void setValue(int value)
    {
        setText(Integer.toString(value));
    }

    public int getValue()
    {
        if (getText().length() == 0)
            return 0;
        try
        {
            return Integer.valueOf(getText());
        }
        catch (NumberFormatException e)
        {
            // This should never happen.
            return 0;
        }
    }

    private class CustomDocument extends PlainDocument
    {
        public CustomDocument()
        {
            super();
            this.setDocumentFilter(new CustomDocumentFilter());
        }
    }

    private class CustomDocumentFilter extends DocumentFilter
    {
        public void insertString(DocumentFilter.FilterBypass fb, int offset,
            String text, AttributeSet attr) throws BadLocationException
        {
            if (text != null)
                super.insertString(fb, offset, filter(text), attr);
        }

        private String filter(String text)
        {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < text.length(); i++)
            {
                char ch = text.charAt(i);
                if (Character.isDigit(ch))
                    b.append(ch);
            }
            return b.toString();
        }
        
        public void remove(DocumentFilter.FilterBypass fb, int offset,
            int length) throws BadLocationException
        {
            super.remove(fb, offset, length);
        }

        public void replace(DocumentFilter.FilterBypass fb, int offset,
            int length, String text, AttributeSet attrs)
            throws BadLocationException
        {
            if (text != null)
                super.replace(fb, offset, length, filter(text), attrs);
        }
    }
}
        