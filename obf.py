from optparse import OptionParser

#class
cShortDeobfToObf = {} #str to str
cDeobfToObf = {}
#field
fSrgToDeobf = {}
fSrgToObf = {}
#method
mSrgToDeobf = {}
mSrgToObf = {} #str to UniqueMethod

class UniqueMethod(object):
	def __init__(self, n, s, oS):
		self.name = n
		self.signature = s
		self.obfSignature = oS

	def __repr__(self):
		return "%s %s %s" % (self.name, self.obfSignature, self.signature)


def init(f, m, p):
	"""Fills mappings files with proper data"""
	with open(f, "r") as file:
		lines = file.read().splitlines()
	for line in lines:
		splitLine = line.split(",")
		fSrgToDeobf[splitLine[0]] = splitLine[1]
	with open(m, "r") as file:
		lines = file.read().splitlines()
	for line in lines:
		splitLine = line.split(",")
	with open(p, "r") as file:
		lines = file.read().splitlines()
	for line in lines:
		splitLine = line.split(" ")
		if "CL:" == splitLine[0]:
			cDeobfToObf[splitLine[2].replace("/", ".")] = splitLine[1]
			cShortDeobfToObf[splitLine[2].split("/")[-1]] = splitLine[1]
		elif "FD:" ==  splitLine[0]:
		    packs = splitLine[2].split("/")
		    fSrgToObf[packs[-1]] = splitLine[1]
		elif "MD:" == splitLine[0]:
		    packs = splitLine[3].split("/")
		    mSrgToObf[packs[-1]] = UniqueMethod(splitLine[1], splitLine[2], splitLine[4])

def SearchShortClass(name):
	if name in cShortDeobfToObf:
		print "?C: %s" % (cShortDeobfToObf[name])
		return True
	return False

def SearchClass(name):
	if name in cDeobfToObf:
		print "C: %s" % (cDeobfToObf[name])
		return True
	return False

def SearchField(name):
	for key in fSrgToDeobf:
		if name == fSrgToDeobf[key]:
			if key in fSrgToObf:
				print "F: %s" % (fSrgToObf[key])
				return True
	return False

def SearchMethod(name):
	for key in mSrgToDeobf:
		if name == mSrgToDeobf[key]:
			if key in mSrgToObf:
				print "M: %s" % (mSrgToObf[key])
				return True
	return False

parser = OptionParser()
(options, args) = parser.parse_args()

init("fields.csv", "methods.csv", "packaged.srg");

##for key in cDeobfToObf:
##	print key, cDeobfToObf[key]
##for key in fSrgToDeobf:
##	print key,  fSrgToDeobf[key]
##for key in fSrgToObf:
##	print key, fSrgToObf[key]
##for key in mSrgToDeobf:
##	print key, mSrgToDeobf[key]
##for key in mSrgToObf:
##	print key, mSrgToObf[key]

if len(args) == 0:
	name = raw_input()
	while len(name) > 0:
		if not SearchClass(name):
			SearchShortClass(name)
		SearchField(name)
		SearchMethod(name)
		name = raw_input()
else:
	for name in args:
		print name
		SearchClass(name)
		SearchField(name)
		SearchMethod(name)
		print
