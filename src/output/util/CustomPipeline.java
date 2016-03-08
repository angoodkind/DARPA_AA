package output.util;
//package tools.nyit;
//
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.lang.reflect.*;
//
//import extractors.nyit.Answer;
//import extractors.nyit.DataNode;
//import extractors.nyit.ExtractionModule;
//import extractors.nyit.Feature;
//import features.bursts.Burst;
//import output.bursts.RemoveFeatures;
//import output.bursts.RemoveInvalid;
//import output.bursts.pp_burst_postprocess;
//import output.bursts.rp_burst_postprocess;
//import output.bursts.pr_burst_postprocess;
//import output.bursts.rr_burst_postprocess;
//
//import java.io.*;
//
//public class CustomPipeline implements ExtractionModule {
//	
//	@Override
//	public Collection<Feature> extract(DataNode data) {
//		
//		///This object holds the intermediate (pre-processed) data
//		HashMap<String, DataNode> newData = new HashMap<String, DataNode>();
//		
//		///This object is a collection of all features and their data
//		HashMap<String, Collection<Feature>> feature_collection = new HashMap<String, Collection<Feature>>();
//		
//		/// Hash map to store different type of bursts
//		HashMap<String, ArrayList<Burst>> BurstArray = new HashMap<String, ArrayList<Burst>>();
//		
//		Pipeline.LoadModules();
//		
//		
//		//////////////////////////This block extract features based on the modules passed in the first line
//		/// This array holds the names of the modules that extract  
//		LinkedList<ExtractionModule> pp_modules = Pipeline.Fetch_PP_Modules();
//		
//		///Iterate through the list of all modules
//		for (ExtractionModule em : pp_modules) {
//			
//			// Get output from the current module selected
//			System.out.println(em.getName());
//			feature_collection.put(em.getName(), em.extract(data));
//			
//			// This try/catch block is for the methods that may or may not be defined
//			try {
//				// Execute this block of code for the object identified by a unique 
//				if (em.getName().equals("PP Burst Metrics") || em.getName().equals("RP Burst Metrics") || em.getName().equals("PR Burst Metrics")
//						|| em.getName().equals("RP2 Burst Metrics") || em.getName().equals("PR2 Burst Metrics") || em.getName().equals("RR Burst Metrics")) {
//					
//					Method m = em.getClass().getMethod("getIntermediateData");
//					
//					@SuppressWarnings("unchecked")
//					DataNode dn = (DataNode) m.invoke(em);
//					
//					newData.put(em.getName(), dn);
//					
//					System.out.println("Number of bursts: " + dn.size());
//					
//					int count = 0;
//					for (Answer a: dn) {
//						count++;
//						System.out.println(count + ": " +a.getFinalText());
//						//String line = ""+dn.getUserID() + "," + a.getQuestionID() + "," + a.getFinalText();
//						//bwPipeline.write(line+"\r\n");
//					}
//					
//					// Get the collection of Burst objects by invoking appropriate method
//					Method m2 = em.getClass().getMethod("getBurstArray");
//					@SuppressWarnings("unchecked")
//					ArrayList<Burst> bArray = (ArrayList<Burst>) m2.invoke(em);
//					
//					// Add burst to a collection
//					BurstArray.put(em.getName(), bArray);
//				}
//				
//				if (em.getName().equals("Module2 name")) {
//					
//				}
//				
//				if (em.getName().equals("Module3 name")) {
//					
//				}
//				
//				
//			} catch (Exception e) {
//				System.out.print("Cannot invoke method");
//				e.printStackTrace();
//			}
//		}
//		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		/////IGNORE/////
//		/*try {
//			bwPipeline.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}*/
//		//////////////////
//		
//       ////////////////////////////////////////////////////This block does feature extraction on the preprocessed answers////////		
//		
//		if (newData.size() > 0) {
//			LinkedList<ExtractionModule> fg_modules = Pipeline.Fetch_FG_Modules();
//			for (String module_name : newData.keySet())
//				for (ExtractionModule em : fg_modules) {
//					
//					//get output from the current module selected and and to previous list
//					//List<Feature> features = (List<Feature>) em.extract(newData.get(module_name));
//					Collection<Feature> features = feature_collection.get(module_name);
//					
//					features.addAll(em.extract(newData.get(module_name)));
//					
//					feature_collection.put(module_name, features);
//				}
//		}
//		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		
//		///////////////////////////////////////////////This block does post processing or adds new features////////////////////
//		for (String batch : feature_collection.keySet()) {
//			switch (batch) {
//				case "PP Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = pp_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//				case "RP Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = rp_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//				case "PR Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = pr_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//				case "RP2 Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = rp_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//				case "PR2 Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = pr_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//				case "RR Burst Metrics": {
//					Collection<Feature> all_features = feature_collection.get(batch);
//					LinkedList<Feature> new_features = rr_burst_postprocess.extract((List<Feature>) feature_collection.get(batch),
//																					BurstArray.get(batch));
//					all_features.addAll(new_features);
//					feature_collection.put(batch, all_features);
//					break;
//				}
//			}
//		}
//		//List<Feature> output_list = (List<Feature>) output;
//		/*for (ExtractionModule em2 : pp_modules) {
//			switch (em2.getName()) {
//			case "PP Burst Metrics": {
//				output.addAll(pp_burst_postprocess.extract(output_list, BurstArray.get("PP Burst Metrics")));
//				break;
//			}
//			case "RP Burst Metrics": {
//				//output.addAll(rp_burst_postprocess.extract(output_list, BurstArray));
//				break;
//			}		
//			}
//		}*/
//		
//		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		//List<Feature> final_list = (List<Feature>) output;
//		LinkedList<Feature> output = new LinkedList<Feature>();
//		
//		///////////////////////////////// Clean, modify or remove features this block ////////////////////////////////////////////
//		String[] delete_list = { "MATTR","Noun_Counts","Verb_Counts","Modifier_Counts","Modal_Counts" };
//		//String[] delete_list = { " " };
//		
//		for (String batch : feature_collection.keySet()) {
//			Collection<Feature> all_features = feature_collection.get(batch);
//			
//			List<Feature> final_list0 = (List<Feature>) all_features;
//			
//			// System.out.println("Original list:");
//			// for (int f0 = 0; f0 < final_list0.size(); f0++) { System.out.println(final_list0.get(f0).getFeatureName()); }
//			
//			RemoveFeatures.Remove((List<Feature>) all_features, delete_list); // all_features list is manipulated by using reference
//			
//			List<Feature> final_list = (List<Feature>) all_features;
//			
//			
//			
//			for (int f = 0; f < final_list.size(); f++) {
//				
//				Feature feat = final_list.get(f);
//				
//				RemoveInvalid.RemoveInfNaN(feat); // all_features list is manipulated by using reference
//				
//				//String new_feat_name = batch + "->" + feat.getFeatureName();
//
//				//feat.setFeatureName(new_feat_name);
//				
//				final_list.set(f, feat);
//			}
//			
//			output.addAll(final_list);
//			
//			//feature_collection.put(batch, all_features);	
//		}
//		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		return output;
//	}
//	
//	@Override
//	public String getName() {
//		return "Custom";
//	}
//	
//	public static class Pipeline {
//		private static boolean state = false;
//		private static LinkedList<ExtractionModule> pp_modules = new LinkedList<ExtractionModule>();
//		private static LinkedList<ExtractionModule> fg_modules = new LinkedList<ExtractionModule>();
//		private static boolean pp = false;
//		private static boolean fg = false;
//		
//	      public static void LoadModules() {
//	    	  if (!state) {
//	    		  
//		    	  state = true;
//		    	  
//		    	  BufferedReader reader = null;
//		    	  FileReader file = null;
//		    	  try {
//		    		  String file_path = System.getProperty("user.dir") + "\\src\\edu\\nyit\\tools\\PipelineModulesList";
//		    		  
//		    		  System.out.println(file_path);
//		    		  file = new FileReader(file_path);
//		    		  reader = new BufferedReader(file);
//		    		  String line = "";
//		    		  
//		    		  while ((line = reader.readLine()) != null) {
//		    			  if (line.startsWith("///Pre-processing")) {
//		    				  pp = true;
//		    				  fg = false;
//		    			  }
//		    			  if (line.startsWith("///Feature Generating")) {
//		    				  fg = true;
//		    				  pp = false;
//		    			  }
//		    			  if (line.startsWith(">")) {
//		    				  String substr = line.substring(1, line.length());
//		    				  //System.out.println(substr);
//		    				  Class em = Class.forName(substr);
//		    				  ExtractionModule te = (ExtractionModule) em.newInstance();		    				  
//		    				 
//		    				  System.out.println("Module Loaded: " + te.getName());
//		    				  if (pp)
//		    					  pp_modules.add(te);
//		    				  if (fg)
//		    					  fg_modules.add(te);
//		    			  }
//		    		  }
//		    	  }
//		    	  catch (Exception e) {
//		    		  throw new RuntimeException(e);
//		    	  }
//		    	  
//		    	  finally {
//		    		  try {
//		    			file.close();
//						reader.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//		    	  }
//	    	  }
//	    	  
//	      }
//	      
//	      public static LinkedList<ExtractionModule> Fetch_PP_Modules() {
//	    	  return pp_modules;
//	      }
//	      
//	      public static LinkedList<ExtractionModule> Fetch_FG_Modules() {
//	    	  return fg_modules;
//	      }
//	      
//	       public void printMessage() {
//	         System.out.println("Message from nested static class"); 
//	      }
//	}
//
//}
