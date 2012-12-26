using System;
using System.IO;
using System.Collections.Generic;

public class Deobf {
	const string FIELDS = "fields.csv", METHODS = "methods.csv", PACKAGED = "packaged.srg";
	
	static void Main(String[] args) {
		Deobf deobf = new Deobf();
		deobf.Init(FIELDS, METHODS, PACKAGED);
		if(args.Length == 0) {
			string name = Console.ReadLine();
			while(name.Length > 0) {
				deobf.SearchClass(name);
				deobf.SearchField(name);
				deobf.SearchMethod(name);
				name = Console.ReadLine();
			}
		} else {
			foreach(string name in args) {
				Console.WriteLine(name);
				deobf.SearchClass(name);
				deobf.SearchField(name);
				deobf.SearchMethod(name);
				Console.WriteLine();
			}
		}
	}
	
	Dictionary<string, string> cDeobfToObf = new Dictionary<string, string>();

	Dictionary<string, string> fSrgToDeobf = new Dictionary<string, string>();
	Dictionary<string, string> fSrgToObf = new Dictionary<string, string>();

	Dictionary<string, string> mSrgToDeobf = new Dictionary<string, string>();
	Dictionary<string, UniqueMethod> mSrgToObf = new Dictionary<string, UniqueMethod>();
	
	public void SearchClass(string name) {
		if(name.IndexOf('.') > 0) {
			name = name.Replace('.', '/');
		}
		if(cDeobfToObf.ContainsKey(name))
			Console.WriteLine("C: {0}", cDeobfToObf[name]);
	}
	
	public void SearchField(string name) {
		if(name.IndexOf('.') > 0) {
			name = name.Replace('.', '/');
		}
		foreach(string key in fSrgToDeobf.Keys) {
			if(name.Equals(fSrgToDeobf[key])) {
				Console.WriteLine("F: {0}", fSrgToObf[key]);
			}
		}
	}
	
	public void SearchMethod(string name) {
		if(name.IndexOf('.') > 0) {
			name = name.Replace('.', '/');
		}
		foreach(string key in mSrgToDeobf.Keys) {
			if(name.Equals(mSrgToDeobf[key])) {
				Console.WriteLine("M: {0}", mSrgToObf[key]);
			}
		}
	}

	public void Init(string f, string m, string p) {
		StreamReader fReader = new StreamReader(f);
		string line = fReader.ReadLine();
		while(line != null && line.Length > 0) {
			string[] splitLine = line.Split(',');
			fSrgToDeobf[splitLine[0]] = splitLine[1];
			line = fReader.ReadLine();
		}
		fReader.Close();
		StreamReader mReader = new StreamReader(m);
		line = mReader.ReadLine();
		while(line != null && line.Length > 0) {
			string[] splitLine = line.Split(',');
			mSrgToDeobf[splitLine[0]] = splitLine[1];
			//Console.WriteLine("{0} {1}", splitLine[1], splitLine[0]);
			line = mReader.ReadLine();
		}
		mReader.Close();
		StreamReader pReader = new StreamReader(p);
		line = pReader.ReadLine();
		while(line != null && line.Length > 0) {
			string[] splitLine = line.Split(' ');
			if("CL:".Equals(splitLine[0])) {
				cDeobfToObf[splitLine[2]] = splitLine[1];
			} else if("FD:".Equals(splitLine[0])) {
				string[] packs = splitLine[2].Split('/');
				fSrgToObf[packs[packs.Length - 1]] = splitLine[1];
			} else if("MD:".Equals(splitLine[0])) {
				string[] packs = splitLine[3].Split('/');
				mSrgToObf[packs[packs.Length - 1]] = new UniqueMethod(splitLine[1], splitLine[2], splitLine[4]);
				//Console.WriteLine("{0}", mSrgToObf[packs[packs.Length - 1]]);
			}
			line = pReader.ReadLine();
		}
		pReader.Close();
	}
}

class UniqueMethod {
	string name;
	string signature;
	string obfSignature;
	public UniqueMethod(string n, string s, string oS){
		name = n;
		signature = s;
		obfSignature = oS;
	}
	
	public override int GetHashCode() {
		return ToString().GetHashCode();
	}

	public override string ToString() {
		return string.Format("{0} {1} {2}", name, obfSignature, signature);
	}
}