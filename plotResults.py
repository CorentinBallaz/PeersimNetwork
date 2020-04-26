# -*- coding: utf-8 -*-
"""
@author: Tanguy
"""


import json
import csv
import matplotlib.pyplot as plt


file = open("results.json")
data = json.load(file)
file.close()
x=[]
infected=[]
rejected=[]
dead=[]
sensible=[]
it=0
nbOfExperience = len(data)

for experience in data :
    eventList = experience["data"]
    eventCount=0
    for event in eventList :
        if it >= 1 :
            x[eventCount]+=event["nbEvent"]
            infected[eventCount]+=event["nbInfected"]
            rejected[eventCount]+=event["nbRejected"]
            dead[eventCount]+=event["nbDeath"]
            sensible[eventCount]+=event["nbSensible"]
            eventCount += 1
        else :
            x.append(event["nbEvent"])
            infected.append(event["nbInfected"])
            rejected.append(event["nbRejected"])
            dead.append(event["nbDeath"])
            sensible.append(event["nbSensible"])
    it+=1

# On divise par le nb de simulation pour avoir une moyenne
for i in range(0,len(x)):
    x[i]=x[i]/nbOfExperience
    infected[i]=infected[i]/nbOfExperience
    rejected[i]=rejected[i]/nbOfExperience
    dead[i]=dead[i]/nbOfExperience
    sensible[i]=sensible[i]/nbOfExperience
    
plt.title('COVID-19 simulation')    
plt.xlim([0, len(x)]) # Modifier len(x) par un int si vous voulez réduire l'échelle pour y voir mieux
plt.ylim([0, sensible[0]+infected[0]])
plt.plot(x,sensible,label="Sensible",color="orange")
plt.plot(x,infected,label="Infected",color="red")
plt.plot(x,rejected,label="Healed",color="cyan")
plt.plot(x,dead,label="Dead",color="grey")
plt.legend(loc = 'upper right')





