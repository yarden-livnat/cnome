package edu.utah.sci.cyclist.core.ui.components;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class Console extends ScrollPane {
	private TextArea _text;
	
	public Console() {
		super();
		build();
		init();
	}
	
	private void init() {
		Logger.getRootLogger().addAppender(new CyclistAppender());
	}
	
	private void build() {
		getStyleClass().add("console");
		setFitToWidth(true);
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		_text = new TextArea();
		setContent(_text);
	}
	
	class CyclistAppender implements Appender {

		public CyclistAppender() {
		}

		@Override
		public void addFilter(Filter filter) {
			System.out.println("addFilter");
			
		}

		@Override
		public void clearFilters() {
			System.out.println("clearFilters");			
		}

		@Override
		public void close() {
			System.out.println("close");			
		}

		@Override
		public void doAppend(LoggingEvent event) {
			StringBuilder builder = new StringBuilder();
			builder.append(_text.getText()).append(event.getMessage()).append("\n");
			try{
				_text.setText(builder.toString());	
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public ErrorHandler getErrorHandler() {
			System.out.println("getErrorHandler");
			return null;
		}

		@Override
		public boolean requiresLayout() {
			System.out.println("requiresLayout?");
			return false;
		}

		@Override
		public void setErrorHandler(ErrorHandler handler) {
			System.out.println("setErrorHandler");
			
		}

		@Override
		public void setLayout(Layout layout) {
			System.out.println("setLayout");
			
		}

		@Override
		public void setName(String name) {
			System.out.println("setName"+name);			
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Layout getLayout() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
