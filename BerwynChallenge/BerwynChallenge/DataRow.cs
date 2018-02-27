using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BerwynChallenge
{
    class DataRow
    {
        private string GUID;
        private int val1=0;
        private int val2=0;
        private string val3;

        public DataRow(string g, int v1, int v2, string v3)
        {
            this.GUID = g;
            this.val1 = v1;
            this.val2 = v2;
            this.val3 = v3;
        }
        public string GetGUID()
        {
            return GUID;
        }
        public int GetVal1()
        {
            return val1;
        }
        public int GetVal2()
        {
            return val2;
        }
        public string GetVal3()
        {
            return val3;
        }
    }
}
