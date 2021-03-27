//Stoian Arsen, group K-10
#include "Record.h"
#include "MyException.h"

using namespace std;

Record::Record(double MinTemp_, int Day_, int Month_,
    double MaxTemp_, double Humid_, double AvgTemp_,
    int Precipitation_, double WindStr_, int Year_) : Record()
{
    MinTemp = MinTemp_;
    Day = Day_;
    Month = Month_;
    MaxTemp = MaxTemp_;
    Humid = Humid_;
    AvgTemp = AvgTemp_;
    Precipitation = Precipitation_;
    WindStr = WindStr_;
    Year = Year_;
    if(!CheckIntegrity()){
        string msg = IntegrityProblem();
        throw ContentError("Values in line is not valid: " + msg);
    }
}

double Record::getMinTemp()const noexcept{ return MinTemp; }
int Record::getDay()const noexcept{ return Day; }
int Record::getMonth()const noexcept{ return Month; }
double Record::getMaxTemp()const noexcept{ return MaxTemp; }
double Record::getHumid()const noexcept{ return Humid; }
double Record::getAvgTemp()const noexcept{ return AvgTemp; }
int Record::getPrecipitation()const noexcept{ return Precipitation; }
double Record::getWindStr()const noexcept{ return WindStr; }
int Record::getYear()const noexcept{ return Year; }

bool Record::operator ==(const Record& other)const noexcept{
    return Year == other.Year && Month == other.Month && Day == other.Day;}
bool Record::operator !=(const Record& other)const noexcept{
    return !(*this == other);}
bool Record::operator <(const Record& other)const noexcept{
    if(Year != other.Year)
        return Year < other.Year;
    if(Month != other.Month)
        return Month < other.Month;
    if(Day != other.Day)
        return Day < other.Day;
    return false;
    ;}
bool Record::operator <=(const Record& other)const noexcept{
    return (*this < other || *this == other);}
bool Record::operator >(const Record& other)const noexcept{
    return !(*this <= other);}
bool Record::operator >=(const Record& other)const noexcept{
    return !(*this < other);}

bool Record::CheckIntegrity()const noexcept{
    return CheckTemp() && CheckDay() && CheckOther(); }

bool Record::CheckTemp()const noexcept{
    return (MinTemp <= MaxTemp) && (MinTemp <= AvgTemp) && (AvgTemp <= MaxTemp); }

bool Record::CheckDay()const noexcept{
    if(Year < 1000 || Year > 9999)
        return false;
    if(Month < 1 || Month > 12)
        return false;
    if(Day < 1 || Day > 31)
        return false;
    //detecting maximum days in the current month:
    int MaxDays = 31;
    switch(Month){
    case 2: MaxDays = 28; break;
    case 4: MaxDays = 30; break;
    case 6: MaxDays = 30; break;
    case 9: MaxDays = 30; break;
    case 11: MaxDays = 30; break;
    }
    if(Month == 2){
        if(Year % 400 == 0)
            MaxDays = 29;
        else{
            if(Year % 100 == 0)
                MaxDays = 28;
            else if(Year % 4 == 0)
                    MaxDays = 29;
            }
    }
    if(Day > MaxDays)
        return false;
    return true;
}

bool Record::CheckOther()const noexcept{
    if(Humid > 100 || Humid < 0)
        return false;
    if(Precipitation < 0)
        return false;
    if(WindStr < 0)
        return false;
    return true;
}

Record::operator string()const{
    string s =
    "MinTemp: " + to_string(getMinTemp()) + "\n" +
    "Day: " + to_string(getDay()) + "\n" +
    "Month: " + to_string(getMonth()) + "\n" +
    "MaxTemp: " + to_string(getMaxTemp()) + "\n" +
    "Humid: " + to_string(getHumid()) + "\n" +
    "AvgTEmp: " + to_string(getAvgTemp()) + "\n" +
    "Precipitation: " + to_string(getPrecipitation()) + "\n" +
    "WindStr: " + to_string(getWindStr()) + "\n" +
    "Year: " + to_string(getYear()) + "\n";
    return s;
}

std::string Record::IntegrityProblem()const{
    string msg = "";
        if(MinTemp > MaxTemp) msg += "MinTemp(" + to_string(MinTemp ) + ") is bigger that MaxTemp" + to_string(MaxTemp) + ") ";
        if(MinTemp > AvgTemp) msg += "MinTemp(" + to_string(MinTemp ) + ") is bigger that AvgTemp" + to_string(AvgTemp) + ") ";
        if(AvgTemp > MaxTemp) msg += "AvgTemp(" + to_string(AvgTemp ) + ") is bigger that MaxTemp" + to_string(MaxTemp) + ") ";
        if(Year < 1000 || Year > 9999)
            msg += "Year(" + to_string(Year) + ") should have 4 digits. ";
        if(Month < 1 || Month > 12)
            msg += "Month(" + to_string(Month) + ") should be from 1 to 12. ";
        if(Day < 1 || Day > 31)
            msg += "Day(" + to_string(Day) + ") should be from 1 to 31. ";
        //detecting maximum days in the current month:
        int MaxDays = 31;
        switch(Month){
        case 2: MaxDays = 28; break;
        case 4: MaxDays = 30; break;
        case 6: MaxDays = 30; break;
        case 9: MaxDays = 30; break;
        case 11: MaxDays = 30; break;
        }
        if(Month == 2){
            if(Year % 400 == 0)
                MaxDays = 29;
            else{
                if(Year % 100 == 0)
                    MaxDays = 28;
                else if(Year % 4 == 0)
                        MaxDays = 29;
                }
        }
        if(Day > MaxDays){
            msg += "There is ";
            if(MaxDays == 29)
                msg += "leap";
            else
                msg += "not leap";
            msg += " year(" + to_string(Year) + "). In this month(" + to_string(Month) + ") can be only " + to_string(MaxDays) + " days (instread of " + to_string(Day) + "). ";
        }
        if(Humid > 100 || Humid < 0)
            msg += "Humid(" + to_string(Humid) + ") should be from 0 to 100. ";
        if(Precipitation < 0)
            msg += "Precipitation(" + to_string(Precipitation) + ") should be not less that 0. ";
        if(WindStr < 0)
            msg +=  "Wind strength(" + to_string(WindStr) + ") should be not less that 0. ";
    return msg;
}


