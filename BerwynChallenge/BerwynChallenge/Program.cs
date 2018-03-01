using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

// Jacob Anderson, 27 Feb 2018

namespace BerwynChallenge
{
    class Program
    {
        static void Main(string[] args)
        {
            // TODO: File path might need to be adjusted after downloading
            FileStream TestData = new FileStream("test.csv", FileMode.Open);
            // Read the file and convert to an array of DataRows
            int records = TotalRecords(TestData);
            TestData.Dispose();
            TestData.Close();
            Console.WriteLine("Total Records: " + records);
            // File is read once to get the number of records, again to populate array
            string line;
            DataRow[] data = new DataRow[records];
            FileStream TestData2 = new FileStream("test.csv", FileMode.Open);
            System.IO.StreamReader reader = new System.IO.StreamReader(TestData2);
            int index = 0;
            // Need to skip the first line
            while ((line = reader.ReadLine()) != null)
            {
                if (index > 0)
                {
                    // Split the string on commas
                    string[] values = line.Split(',');
                    // Create a row object (unsafe parsing, I'm just assuming values are safe)
                    int v1, v2 = 0;
                    v1 = int.Parse(values[1].Trim(' ','"'));
                    v2 = int.Parse(values[2].Trim(' ','"'));
                    DataRow datum = new DataRow(values[0], v1, v2, values[3]);
                    // Store in array
                    data[index - 1] = datum;
                    index++;
                }
                else
                {
                    index++;
                }
            }
            reader.Dispose();
            reader.Close();
            TestData2.Dispose();
            TestData2.Close();
            // Now that the array is constructed, pass it to the helper methods
            string mostSum = LargestSum(data);
            Console.WriteLine("Largest Sum: "+mostSum);
            List<int> dupIDs = DupGUID(data, records);
            double avLength = AverageLength(data,records);
            Console.WriteLine("Average length of Val3 is: " + avLength);
            Console.WriteLine("Press Enter to create Output File");
            Console.ReadLine();
            // Console output complete. Now output CSV file.
            FileStream outputData = new FileStream("output.csv", FileMode.Create);
            System.IO.StreamWriter writer = new StreamWriter(outputData);
            int i = 0;
            writer.WriteLine("GUID,Val1+Val2,IsDuplicateGUID,Val3Length>Average");
            while (i < data.Length - 1 && data[i] != null)
            {
                string output1 = data[i].GetGUID();
                int output2 = data[i].GetVal1() + data[i].GetVal2();
                string output3 = "N";
                if (dupIDs.Contains(i))
                {
                    output3 = "Y";
                }
                string output4 = "N";
                if (data[i].GetVal3().Trim('"').Length > avLength)
                {
                    output4 = "Y";
                }
                var outputLine = string.Format("{0},{1},{2},{3}", output1, output2.ToString(), output3, output4);
                writer.WriteLine(outputLine);              
                i++;
            }
        }
        static int TotalRecords(FileStream file)
        {
            // Outputs number of records in the file
            int total = 0;
            string line;
            System.IO.StreamReader reader = new System.IO.StreamReader(file);
            while((line = reader.ReadLine()) != null)
            {
                total++;
            }
            reader.Dispose();
            reader.Close();
            return total;
        }
        static string LargestSum(DataRow[] data)
        {
            // Outputs largest sum of rows Val1 and Val2
            int maxSum = 0;
            int currentSum = 0;
            int maxRow = 0;
            string maxID = " ";
            int i = 0;
            while(i < data.Length-1 && data[i] != null)
            {
                currentSum = data[i].GetVal1() + data[i].GetVal2();
                if(currentSum > maxSum)
                {
                    maxSum = currentSum;
                    maxID = data[i].GetGUID();
                    maxRow = i+1;
                }
                i++;
            }
            // maxRow+1 is so console output is consistent with CSV output
            string output = maxSum + " with GUID " + maxID + " at row " + (maxRow+1) + "";
            return output;
        }
        static List<int> DupGUID(DataRow[] data, int records)
        {
            // Outputs any duplicate IDs found
            int[] output = new int[100];
            List<int> duplicates = new List<int>();
            DataRow[] seen = new DataRow[records];
            int i = 0;
            while(i < data.Length && data[i] != null)
            {
                string nextGUID = data[i].GetGUID();
                int j = 0;
                while(j <= i && seen[j] != null)
                {
                    // Scan already-seen data for duplicate GUIDs
                    if(nextGUID == seen[j].GetGUID())
                    {
                        // j+2 and i+2 are so console output is consistent with CSV output
                        string outputLine = "Duplicate GUIDs between rows " + (j+2) + " and " + (i+2) + "";
                        Console.WriteLine(outputLine);
                        if(!duplicates.Contains(i))
                        {
                            duplicates.Add(i);
                        }
                        if(!duplicates.Contains(j))
                        {
                            duplicates.Add(j);
                        }
                    }
                    j++;
                }
                seen[i] = data[i];
                i++;
            }
            return duplicates;
        }
        static double AverageLength(DataRow[] data, int records)
        {
            // Average length of Val3 string
            int totalLength = 0;
            int i = 0;
            while(i < data.Length && data[i] != null)
            {
                totalLength = totalLength + data[i].GetVal3().Trim('"').Length;
                //Console.WriteLine(data[i].GetVal3());
                //Console.ReadLine();
                i++;
            }
            // Typecasting integers to doubles for division
            //Console.WriteLine(totalLength);
            //Console.WriteLine(records);
            double avLength = (double)totalLength / (double)records;
            return avLength;
        }
    }
}
